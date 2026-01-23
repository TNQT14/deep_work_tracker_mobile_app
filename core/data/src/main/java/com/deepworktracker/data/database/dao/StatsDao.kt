package com.deepworktracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deepworktracker.data.database.entity.DailyStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {
    
    @Query("SELECT * FROM daily_stats WHERE date = :date")
    suspend fun getDailyStats(date: String): DailyStatsEntity?
    
    @Query("SELECT * FROM daily_stats WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getStatsByDateRange(startDate: String, endDate: String): Flow<List<DailyStatsEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: DailyStatsEntity)
    
    @Query("SELECT SUM(total_focus_time) FROM daily_stats WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalFocusTimeInRange(startDate: String, endDate: String): Long?
}
