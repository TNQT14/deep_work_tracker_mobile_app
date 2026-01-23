package com.deepworktracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "insights",
    indices = [Index(value = ["generated_at"])]
)
data class InsightEntity(
    @PrimaryKey
    val id: String,
    
    val type: String, // "BEST_TIME_WINDOW", "OPTIMAL_SESSION_LENGTH", etc.
    
    val message: String,
    
    @ColumnInfo(name = "generated_at")
    val generatedAt: Long, // Unix timestamp
    
    val confidence: Float?, // 0.0 - 1.0, null for rule-based
    
    // Additional data as JSON string
    val data: String?, // JSON string for flexible data storage
    
    @ColumnInfo(name = "is_dismissed")
    val isDismissed: Boolean = false
)
