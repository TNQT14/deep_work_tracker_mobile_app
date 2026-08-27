package com.deepworktracker.session.domain.usecase

import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.error.DeepWorkError
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.InterruptionRepository
import com.deepworktracker.domain.repository.SessionRepository
import com.deepworktracker.session.domain.interruption.calculateFocusedDuration
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import javax.inject.Inject

class EndSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val interruptionRepository: InterruptionRepository,
) {
    suspend operator fun invoke(): Result<FocusSession> {
        return try {
            val activeSession = sessionRepository.observeActiveSession().first()
                ?: return Result.Error(DeepWorkError.NoActiveSession)

            val now = Clock.System.now()
            val totalDuration = (now - activeSession.startTime).inWholeMilliseconds
            val interruptions = interruptionRepository.getInterruptionsBySession(activeSession.id)
                .first()
                .map { open ->
                    if (open.endTime != null) return@map open
                    val closed = open.copy(
                        endTime = now,
                        duration = (now - open.startTime).inWholeMilliseconds.coerceAtLeast(0),
                    )
                    interruptionRepository.updateInterruption(closed)
                    closed
                }
            val focusedDuration = calculateFocusedDuration(totalDuration, interruptions)

            val endedSession = activeSession.copy(
                endTime = now,
                totalDuration = totalDuration,
                focusedDuration = focusedDuration,
                actualFocusedMinutes = (focusedDuration / 60_000L).toInt(),
                interruptions = interruptions,
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
