package com.deepworktracker.session.domain.usecase

import com.deepworktracker.domain.repository.SessionRepository
import javax.inject.Inject

class GetRecentCategoriesUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(limit: Int = 8): List<String> {
        return sessionRepository.getRecentCategories(limit)
    }
}

