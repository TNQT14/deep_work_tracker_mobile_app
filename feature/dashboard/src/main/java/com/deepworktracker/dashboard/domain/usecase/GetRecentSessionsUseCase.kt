package com.deepworktracker.dashboard.domain.usecase

import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * Use case để lấy các sessions gần đây (7 ngày qua)
 */
class GetRecentSessionsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(limit: Int = 10): Result<List<FocusSession>> {
        return try {
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
            
            val sevenDaysAgo = today.minus(7, kotlinx.datetime.DateTimeUnit.DAY)
            
            // Lấy sessions trong 7 ngày qua
            val sessions = sessionRepository.getSessionsByDateRange(sevenDaysAgo, today).first()
            
            // Sắp xếp theo thời gian bắt đầu (mới nhất trước) và giới hạn số lượng
            val sortedSessions = sessions
                .sortedByDescending { it.startTime }
                .take(limit)
            
            Result.Success(sortedSessions)
        } catch (e: Exception) {
            Result.Error(com.deepworktracker.domain.error.DeepWorkError.UnknownError(
                e.message ?: "Failed to get recent sessions"
            ))
        }
    }
}
