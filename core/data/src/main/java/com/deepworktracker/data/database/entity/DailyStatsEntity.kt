package com.deepworktracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_stats",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailyStatsEntity(
    @PrimaryKey
    val date: String, // YYYY-MM-DD format
    
    @ColumnInfo(name = "total_focus_time")
    val totalFocusTime: Long, // milliseconds
    
    @ColumnInfo(name = "session_count")
    val sessionCount: Int,
    
    @ColumnInfo(name = "interruption_count")
    val interruptionCount: Int,
    
    @ColumnInfo(name = "average_session_duration")
    val averageSessionDuration: Long, // milliseconds
    
    @ColumnInfo(name = "best_focus_hour")
    val bestFocusHour: Int?, // 0-23, hour with most focus time
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
