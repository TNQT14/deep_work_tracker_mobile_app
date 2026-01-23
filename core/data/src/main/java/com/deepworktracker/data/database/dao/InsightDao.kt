package com.deepworktracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deepworktracker.data.database.entity.InsightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InsightDao {
    
    @Query("SELECT * FROM insights WHERE is_dismissed = 0 ORDER BY generated_at DESC LIMIT :limit")
    fun getRecentInsights(limit: Int = 10): Flow<List<InsightEntity>>
    
    @Query("SELECT * FROM insights WHERE type = :type AND is_dismissed = 0 ORDER BY generated_at DESC LIMIT 1")
    suspend fun getLatestInsightByType(type: String): InsightEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(insight: InsightEntity)
    
    @Query("UPDATE insights SET is_dismissed = 1 WHERE id = :id")
    suspend fun dismissInsight(id: String)
    
    @Query("DELETE FROM insights WHERE generated_at < :beforeTimestamp")
    suspend fun deleteOldInsights(beforeTimestamp: Long)
}
