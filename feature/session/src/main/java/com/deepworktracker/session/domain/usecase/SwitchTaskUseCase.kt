package com.deepworktracker.session.domain.usecase

import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.error.DeepWorkError
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Ends the active session at [now] and starts a new row for [newTodoId] in one
 * Room transaction, keeping the same [FocusSession.sittingId] so one continuous
 * sitting can span tasks without a 0-active-row window.
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
            when (val computed = endSession.computeEndedSession()) {
                is Result.Error -> computed
                is Result.Success -> {
                    val ended = computed.data
                    val now = ended.endTime
                        ?: return Result.Error(DeepWorkError.UnknownError("Ended session missing endTime"))
                    val sittingId = ended.sittingId ?: ended.id
                    val next = FocusSession(
                        id = UUID.randomUUID().toString(),
                        goal = newGoal,
                        category = ended.category,
                        startTime = now,
                        endTime = null,
                        totalDuration = 0L,
                        focusedDuration = 0L,
                        tag = ended.tag,
                        note = null,
                        todoId = newTodoId,
                        sittingId = sittingId,
                        focusMinutes = ended.focusMinutes,
                        breakMinutes = ended.breakMinutes,
                        repeat = ended.repeat,
                        alertMode = ended.alertMode,
                    )
                    val switchResult = sessionRepository.switchActiveSession(ended, next)
                    if (switchResult.isSuccess) {
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
