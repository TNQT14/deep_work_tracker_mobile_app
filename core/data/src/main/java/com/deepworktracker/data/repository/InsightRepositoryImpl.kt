package com.deepworktracker.data.repository

import com.deepworktracker.data.database.dao.InsightDao
import com.deepworktracker.data.mapper.InsightMapper
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.repository.InsightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InsightRepositoryImpl @Inject constructor(
    private val insightDao: InsightDao,
    private val mapper: InsightMapper
) : InsightRepository {
    
    override fun getRecentInsights(limit: Int): Flow<List<Insight>> {
        return insightDao.getRecentInsights(limit)
            .map { entities -> entities.map { mapper.toDomain(it) } }
    }
    
    override suspend fun getLatestInsightByType(type: String): Insight? {
        return insightDao.getLatestInsightByType(type)?.let { mapper.toDomain(it) }
    }
    
    override suspend fun saveInsight(insight: Insight): Result<Unit> {
        return try {
            val entity = mapper.toEntity(insight)
            insightDao.insertInsight(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun dismissInsight(id: String): Result<Unit> {
        return try {
            insightDao.dismissInsight(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
