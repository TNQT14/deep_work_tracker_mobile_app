package com.deepworktracker.dashboard.domain.usecase

import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.analytics.FocusAnalytics
import com.deepworktracker.domain.analytics.FocusHeatmapAggregator
import com.deepworktracker.domain.analytics.FocusScoreCalculator
import com.deepworktracker.domain.error.DeepWorkError
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject


class GetFocusAnalyticsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(
        period: AnalyticsPeriod,
        zone: TimeZone = TimeZone.currentSystemDefault(),
    ): Result<FocusAnalytics> {
        return try {
            val today = Clock.System.now().toLocalDateTime(zone).date
            val (from, to) = period.dateRange(today)

            val sessions = sessionRepository.getSessionsByDateRange(from, to).first()
            val completed = sessions.filter { it.endTime != null }

            val totalFocusedMs = completed.sumOf { it.focusedDuration }
            val totalFocusedSeconds = totalFocusedMs / MILLIS_PER_SECOND
            val totalFocusedMinutes = totalFocusedMs / MILLIS_PER_MINUTE
            val bestHours = FocusHeatmapAggregator.bestFocusHours(sessions, zone)

            val analytics = FocusAnalytics(
                period = period,
                focusScore = FocusScoreCalculator.score(sessions),
                totalFocusedSeconds = totalFocusedSeconds,
                sessionCount = completed.size,
                avgSessionMinutes = if (completed.isNotEmpty()) {
                    totalFocusedMinutes / completed.size
                } else {
                    0L
                },
                bestFocusHour = bestHours.firstOrNull(),
                dailyTrendMinutes = dailyTrend(completed, from, to, zone),
                heatmap = FocusHeatmapAggregator.aggregate(sessions, zone),
                bestFocusHours = bestHours,
            )
            Result.Success(analytics)
        } catch (e: Exception) {
            Result.Error(DeepWorkError.UnknownError(e.message ?: "Failed to get analytics"))
        }
    }

    private fun dailyTrend(
        sessions: List<FocusSession>,
        from: LocalDate,
        to: LocalDate,
        zone: TimeZone,
    ): List<Long> {
        val byDate = sessions.groupBy { it.startTime.toLocalDateTime(zone).date }
        val result = mutableListOf<Long>()
        var day = from
        while (day <= to) {
            val minutes = byDate[day]?.sumOf { it.focusedDuration }?.div(MILLIS_PER_MINUTE) ?: 0L
            result += minutes
            day = day.plus(1, DateTimeUnit.DAY)
        }
        return result
    }


    private companion object {
        const val MILLIS_PER_SECOND = 1_000L
        const val MILLIS_PER_MINUTE = 60_000L
    }
}