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
 * Gom [StatsWindow] (current + previous + interruptionsByDay) -> chạy tất cả rule
 * -> lưu insight vào Room. Chạy nền bởi WorkManager (Step 7), không đụng UI.
 * @return số insight mới đã lưu.
 */
class GenerateInsightsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val interruptionRepository: InterruptionRepository,
    private val insightRepository: InsightRepository,
    private val getFocusAnalytics: GetFocusAnalyticsUseCase,
    private val rules: List<@JvmSuppressWildcards InsightRule>,
) {
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
                if (insightRepository.saveInsight(insight) is Result.Success<*>) saved++
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