package com.deepworktracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "focus_sessions",
    indices = [
        Index(value = ["start_time"]),
        Index(value = ["date"])
    ]
)
data class FocusSessionEntity(
    @PrimaryKey
    val id: String,
    
    val goal: String,
    
    @ColumnInfo(name = "start_time")
    val startTime: Long, // Unix timestamp in milliseconds
    
    @ColumnInfo(name = "end_time")
    val endTime: Long?, // Null if session is active
    
    @ColumnInfo(name = "total_duration")
    val totalDuration: Long, // milliseconds
    
    @ColumnInfo(name = "focused_duration")
    val focusedDuration: Long, // milliseconds (total - interruptions)
    
    val tag: String?,
    val note: String?,
    
    // Computed field for querying by date
    @ColumnInfo(name = "date")
    val date: String, // YYYY-MM-DD format
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
