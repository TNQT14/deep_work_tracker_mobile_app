package com.deepworktracker.dashboard.domain.usecase

import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import javax.inject.Inject
import com.deepworktracker.common.result.Result

class GetAllSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Result<List<FocusSession>> {
        return try {
            val allSession =sessionRepository.getAllSessions()
            Result.Success(allSession)
        }catch (e: Exception){
            Result.Error(e)

        }
    }
}