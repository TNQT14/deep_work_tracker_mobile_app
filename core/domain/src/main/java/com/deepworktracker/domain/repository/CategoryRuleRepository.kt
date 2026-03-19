package com.deepworktracker.domain.repository

import com.deepworktracker.domain.model.CategoryRule
import kotlinx.coroutines.flow.Flow

interface CategoryRuleRepository {
    fun observeRules(): Flow<List<CategoryRule>>
    suspend fun upsert(rule: CategoryRule): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
}

