package com.deepworktracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.deepworktracker.domain.model.AlertMode

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

    val category: String?,
    
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
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "todo_id")
    val todoId: String? = null,

    @ColumnInfo(name = "focus_minutes")
    val focusMinutes: Int = 25,

    @ColumnInfo(name = "break_minutes")
    val breakMinutes: Int = 5,

    val repeat: Boolean = false,

    @ColumnInfo(name = "alert_mode")
    val alertMode: AlertMode = AlertMode.NOTIFY,

    @ColumnInfo(name = "actual_focused_minutes")
    val actualFocusedMinutes: Int = 0,

    val cycles: Int = 0,
)
