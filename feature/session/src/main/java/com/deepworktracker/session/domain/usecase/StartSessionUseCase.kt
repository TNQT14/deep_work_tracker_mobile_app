package com.deepworktracker.session.domain.usecase

import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.error.DeepWorkError
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class StartSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(
        goal: String,
        category: String?,
        tag: String?
    ): Result<FocusSession> {
        return try {
            val activeSession = sessionRepository.observeActiveSession().first()
            if (activeSession != null) {
                return Result.Error(DeepWorkError.ActiveSessionExists)
            }

            val now = Clock.System.now()
            val session = FocusSession(
                id = UUID.randomUUID().toString(),
                goal = goal,
                category = category,
                startTime = now,
                endTime = null,
                totalDuration = 0L,
                focusedDuration = 0L,
                tag = tag,
                note = null
            )
            
            val saveResult = sessionRepository.saveSession(session)
            if (saveResult.isSuccess) {
                Result.Success(session)
            } else {
                Result.Error(DeepWorkError.DatabaseError)
            }
        } catch (e: Exception) {
            Result.Error(DeepWorkError.UnknownError(e.message ?: "Unknown error"))
        }
    }
}
