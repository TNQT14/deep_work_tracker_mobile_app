package com.deepworktracker.domain.repository

import com.deepworktracker.domain.model.Insight
import kotlinx.coroutines.flow.Flow

interface InsightRepository {
    fun getRecentInsights(limit: Int = 10): Flow<List<Insight>>
    suspend fun getLatestInsightByType(type: String): Insight?
    suspend fun saveInsight(insight: Insight): Result<Unit>
    suspend fun dismissInsight(id: String): Result<Unit>
}
