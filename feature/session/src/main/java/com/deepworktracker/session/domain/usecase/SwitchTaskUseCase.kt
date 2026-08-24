package com.deepworktracker.session.domain.usecase

import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.error.DeepWorkError
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

/**
 * Ends the active session at [now] and starts a new row for [newTodoId],
 * keeping the same [FocusSession.sittingId] so one continuous sitting can span tasks.
 */
class SwitchTaskUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val endSession: EndSessionUseCase,
) {
    suspend operator fun invoke(
        newTodoId: String,
        newGoal: String,
    ): Result<FocusSession> {
        return try {
            val oldSession = sessionRepository.observeActiveSession().first()
                ?: return Result.Error(DeepWorkError.NoActiveSession)
            val sittingId = oldSession.sittingId ?: oldSession.id

            when (val endResult = endSession()) {
                is Result.Error -> endResult
                is Result.Success -> {
                    val ended = endResult.data
                    val now = ended.endTime
                        ?: return Result.Error(DeepWorkError.UnknownError("Ended session missing endTime"))
                    val next = FocusSession(
                        id = UUID.randomUUID().toString(),
                        goal = newGoal,
                        category = oldSession.category,
                        startTime = now,
                        endTime = null,
                        totalDuration = 0L,
                        focusedDuration = 0L,
                        tag = oldSession.tag,
                        note = null,
                        todoId = newTodoId,
                        sittingId = sittingId,
                        focusMinutes = oldSession.focusMinutes,
                        breakMinutes = oldSession.breakMinutes,
                        repeat = oldSession.repeat,
                        alertMode = oldSession.alertMode,
                    )
                    val saveResult = sessionRepository.saveSession(next)
                    if (saveResult.isSuccess) {
                        Result.Success(next)
                    } else {
                        Result.Error(DeepWorkError.DatabaseError)
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(DeepWorkError.UnknownError(e.message ?: "Unknown error"))
        }
    }
}
