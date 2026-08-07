package com.deepworktracker.dashboard.domain.usecase

import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.analytics.FocusAnalytics
import com.deepworktracker.domain.analytics.FocusScoreCalculator
import com.deepworktracker.domain.error.DeepWorkError
import com.deepworktracker.domain.insights.InsightRule
import com.deepworktracker.domain.insights.StatsWindow
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.repository.InsightRepository
import com.deepworktracker.domain.repository.InterruptionRepository
import com.deepworktracker.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * [Domain — Feature orchestration] [DI]
 * Assembles a [StatsWindow] (current period + previous period + per-day interruption
 * counts), runs every injected [InsightRule] against it, and persists whatever fires
 * via [InsightRepository]. Invoked from background (GenerateInsightsWorker, periodic
 * 24h) — never touches UI/ViewModel state directly; the UI picks up new rows reactively
 * through InsightRepository.getRecentInsights()'s Flow.
 *
 * `rules` is injected as a List<InsightRule> — see InsightModule (Hilt @Provides) for
 * how the 4 concrete rule classes are assembled into that list.
 */
class GenerateInsightsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val interruptionRepository: InterruptionRepository,
    private val insightRepository: InsightRepository,
    private val getFocusAnalytics: GetFocusAnalyticsUseCase,
    private val rules: List<@JvmSuppressWildcards InsightRule>,
) {
    /**
     * Input: period (AnalyticsPeriod, default WEEK), zone (TimeZone, default device zone)
     * Process:
     *   1. Reuse GetFocusAnalyticsUseCase (feature #6) to get `current` period aggregates.
     *   2. Fetch this period's sessions (for OptimalSessionLengthRule).
     *   3. Compute the immediately preceding period of the same length and its
     *      focusScore/sessionCount only (all DecliningTrendRule needs) -> `previous`.
     *   4. Fetch interruptions in range and group by isoDayNumber in Kotlin (not SQL
     *      GROUP BY) — start_time is stored as UTC epoch millis, so bucketing by
     *      calendar day must go through kotlinx.datetime + the caller's TimeZone.
     *   5. Build the StatsWindow; bail early with Result.Success(0) if
     *      !window.hasEnoughData (see StatsWindow.MIN_SESSIONS guardrail).
     *   6. Run every rule, collect non-null Insights.
     *   7. For each produced Insight, skip it if the same InsightType was already
     *      saved earlier the same day (de-dupe against notification spam), otherwise
     *      persist via insightRepository.saveInsight.
     * Output: Result<Int> — Success(count of newly saved insights, 0 if guardrail
     *         short-circuited) or Error(DeepWorkError) on any exception.
     */
    suspend operator fun invoke(
        period: AnalyticsPeriod = AnalyticsPeriod.WEEK,
        zone: TimeZone = TimeZone.currentSystemDefault(),
    ): Result<Int> {
        return try {
            val today = Clock.System.now().toLocalDateTime(zone).date
            val (from, to) = period.dateRange(today)

            val current = when (val r = getFocusAnalytics(period, zone)) {
                is Result.Success -> r.data
                is Result.Error -> return Result.Error(r.exception)
            }

            val sessions = sessionRepository.getSessionsByDateRange(from, to).first()

            val lengthDays = daysInclusive(from, to)
            val prevTo = from.minus(1, DateTimeUnit.DAY)
            val prevFrom = prevTo.minus(lengthDays - 1, DateTimeUnit.DAY)
            val prevSessions = sessionRepository.getSessionsByDateRange(prevFrom, prevTo).first()
            val previous: FocusAnalytics? =
                if (prevSessions.isEmpty()) null
                else FocusAnalytics.empty(period).copy(
                    focusScore = FocusScoreCalculator.score(prevSessions),
                    sessionCount = prevSessions.count { it.endTime != null },
                )

            val fromInstant = from.atStartOfDayIn(zone)
            val toInstant = to.plus(1, DateTimeUnit.DAY).atStartOfDayIn(zone)
            val interruptionsByDay = interruptionRepository
                .getInterruptionsBetween(fromInstant, toInstant)
                .groupingBy { it.startTime.toLocalDateTime(zone).dayOfWeek.isoDayNumber }
                .eachCount()

            val window = StatsWindow(
                period = period,
                current = current,
                previous = previous,
                sessions = sessions,
                interruptionsByDay = interruptionsByDay,
                now = Clock.System.now(),
            )
            if (!window.hasEnoughData) return Result.Success(0)

            val produced: List<Insight> = rules.mapNotNull { it.evaluate(window) }
            var saved = 0
            for (insight in produced) {
                val latest = insightRepository.getLatestInsightByType(insight.type.name)
                if (latest != null && sameDay(latest.generatedAt, insight.generatedAt, zone)) continue
                if (insightRepository.saveInsight(insight).isSuccess) saved++
            }
            Result.Success(saved)
        } catch (e: Exception) {
            Result.Error(DeepWorkError.UnknownError(e.message ?: "Failed to generate insights"))
        }
    }

    private fun daysInclusive(from: LocalDate, to: LocalDate): Int {
        var day = from
        var count = 0
        while (day <= to) {
            count++
            day = day.plus(1, DateTimeUnit.DAY)
        }
        return count
    }

    private fun sameDay(a: Instant, b: Instant, zone: TimeZone): Boolean =
        a.toLocalDateTime(zone).date == b.toLocalDateTime(zone).date
}