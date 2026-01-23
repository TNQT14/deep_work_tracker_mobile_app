package com.deepworktracker.session.domain.usecase

import com.deepworktracker.domain.repository.SessionRepository
import javax.inject.Inject

class GetRecentGoalsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke() : List<String>{
        return sessionRepository.getRecentGoal()
    }
}