package com.deepworktracker.dashboard.domain.usecase

import com.deepworktracker.domain.error.DeepWorkError
import com.deepworktracker.domain.repository.SessionRepository
import com.deepworktracker.domain.repository.UserPreferencesRepository
import com.deepworktracker.domain.streak.StreakCalculator
import com.deepworktracker.domain.streak.StreakResult
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import com.deepworktracker.common.result.Result


class GetStreakUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(
        zone: TimeZone = TimeZone.currentSystemDefault(),
    ): Result<StreakResult> {
        return try {
            val today = Clock.System.now().toLocalDateTime(zone).date
            val from = today.minus(WINDOW_DAYS - 1, DateTimeUnit.DAY)
            val goalMinutes = userPreferencesRepository.getPreferences().dailyGoalMinutes
            if (goalMinutes <= 0) {
                return Result.Success(
                    StreakResult.EMPTY
                )
            }
            val dailyMillis = sessionRepository.getDailyFocusedMillis(from, today).first()
            val dailyMinutes = dailyMillis.mapValues { (_, ms) -> ms / MILLIS_PER_MINUTE }
            Result.Success(
                StreakCalculator.calculate(
                    dailyMinutes = dailyMinutes,
                    goalMinutes = goalMinutes,
                    today = today
                )
            )

        } catch (e: Exception) {
            Result.Error(DeepWorkError.UnknownError(e.message ?: "Failed to get streak"))
        }
    }

    private companion object {
        const val WINDOW_DAYS = 365
        const val MILLIS_PER_MINUTE = 60_000L
    }
}