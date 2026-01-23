package com.deepworktracker.dashboard.domain.usecase

import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.model.DailyStats
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import com.deepworktracker.domain.repository.StatsRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * Use case để lấy thống kê hôm nay
 * Nếu chưa có stats được tính toán, sẽ tính toán từ sessions
 */
class GetTodayStatsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val statsRepository: StatsRepository
) {
    suspend operator fun invoke(): Result<TodayStats> {
        return try {
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
            
            // Lấy sessions hôm nay
            val sessions = sessionRepository.getSessionsByDate(today).first()
            
            // Tính toán stats từ sessions
            val totalFocusTime = sessions.sumOf { it.totalDuration }
            val sessionCount = sessions.size
            val completedSessions = sessions.filter { it.endTime != null }
            val averageDuration = if (completedSessions.isNotEmpty()) {
                completedSessions.map { it.totalDuration }.average().toLong()
            } else {
                0L
            }
            
            // Tính best focus hour (giờ có nhiều focus time nhất)
            val hourFocusMap = mutableMapOf<Int, Long>()
            sessions.forEach { session ->
                val hour = session.startTime
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .hour
                hourFocusMap[hour] = (hourFocusMap[hour] ?: 0L) + session.totalDuration
            }
            val bestFocusHour = hourFocusMap.maxByOrNull { it.value }?.key
            
            val stats = TodayStats(
                date = today,
                totalFocusTime = totalFocusTime,
                sessionCount = sessionCount,
                averageSessionDuration = averageDuration,
                bestFocusHour = bestFocusHour
            )
            
            Result.Success(stats)
        } catch (e: Exception) {
            Result.Error(com.deepworktracker.domain.error.DeepWorkError.UnknownError(
                e.message ?: "Failed to get today's stats"
            ))
        }
    }
}

/**
 * Data class để hiển thị stats hôm nay
 */
data class TodayStats(
    val date: LocalDate,
    val totalFocusTime: Long, // milliseconds
    val sessionCount: Int,
    val averageSessionDuration: Long, // milliseconds
    val bestFocusHour: Int? // 0-23
)
