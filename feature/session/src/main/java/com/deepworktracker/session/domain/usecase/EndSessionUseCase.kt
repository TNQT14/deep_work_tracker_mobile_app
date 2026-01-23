package com.deepworktracker.session.domain.usecase

import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.error.DeepWorkError
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import javax.inject.Inject

class EndSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Result<FocusSession> {
        return try {
            val activeSession = sessionRepository.observeActiveSession().first()
                ?: return Result.Error(DeepWorkError.NoActiveSession)
            
            val now = Clock.System.now()
            val totalDuration = (now - activeSession.startTime).inWholeMilliseconds
            
            val endedSession = activeSession.copy(
                endTime = now,
                totalDuration = totalDuration,
                focusedDuration = totalDuration // Simplified - should subtract interruptions
            )
            
            val updateResult = sessionRepository.updateSession(endedSession)
            if (updateResult.isSuccess) {
                Result.Success(endedSession)
            } else {
                Result.Error(DeepWorkError.DatabaseError)
            }
        } catch (e: Exception) {
            Result.Error(DeepWorkError.UnknownError(e.message ?: "Unknown error"))
        }
    }
}
