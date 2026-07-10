package com.deepworktracker.session.domain.usecase

import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import javax.inject.Inject

class GetSessionByIdUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(id: String): FocusSession? {
        return sessionRepository.getSessionById(id)
    }
}
