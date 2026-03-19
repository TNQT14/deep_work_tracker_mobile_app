package com.deepworktracker.data.repository

import com.deepworktracker.data.database.dao.CategoryRuleDao
import com.deepworktracker.data.mapper.CategoryRuleMapper
import com.deepworktracker.domain.model.CategoryRule
import com.deepworktracker.domain.repository.CategoryRuleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRuleRepositoryImpl @Inject constructor(
    private val dao: CategoryRuleDao,
    private val mapper: CategoryRuleMapper
) : CategoryRuleRepository {
    override fun observeRules(): Flow<List<CategoryRule>> {
        return dao.observeRules().map { list -> list.map { mapper.toDomain(it) } }
    }

    override suspend fun upsert(rule: CategoryRule): Result<Unit> {
        return try {
            dao.upsert(mapper.toEntity(rule))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(id: String): Result<Unit> {
        return try {
            dao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

