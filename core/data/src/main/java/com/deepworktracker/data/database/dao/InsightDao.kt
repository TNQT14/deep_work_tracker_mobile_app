package com.deepworktracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deepworktracker.data.database.entity.InsightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InsightDao {
    
    // Secondary sort by id breaks ties deterministically. Ties are the NORMAL case, not
    // an edge case: every rule fired within one GenerateInsightsUseCase run shares the
    // exact same `window.now` timestamp, so 2-4 rows routinely share generated_at.
    // SQLite does not guarantee a stable row order for ties across separate query
    // executions -- without this tiebreaker, the list order (and therefore which
    // insight sits at which pager page) could silently shuffle between recompositions.
    @Query("SELECT * FROM insights WHERE is_dismissed = 0 ORDER BY generated_at DESC, id ASC LIMIT :limit")
    fun getRecentInsights(limit: Int = 10): Flow<List<InsightEntity>>

    @Query("SELECT * FROM insights WHERE type = :type AND is_dismissed = 0 ORDER BY generated_at DESC, id ASC LIMIT 1")
    suspend fun getLatestInsightByType(type: String): InsightEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(insight: InsightEntity)
    
    @Query("UPDATE insights SET is_dismissed = 1 WHERE id = :id")
    suspend fun dismissInsight(id: String)
    
    @Query("DELETE FROM insights WHERE generated_at < :beforeTimestamp")
    suspend fun deleteOldInsights(beforeTimestamp: Long)
}
