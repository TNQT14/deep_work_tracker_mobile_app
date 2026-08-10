package com.deepworktracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Duration

@Entity(
    tableName = "interruptions",
    foreignKeys = [
        ForeignKey(
            entity = FocusSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["session_id"]),
        Index(value = ["start_time"])
    ]
)
data class InterruptionEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "session_id")
    val sessionId: String,

    @ColumnInfo(name = "start_time")
    val startTime: Long, // Unix timestamp in milliseconds

    @ColumnInfo(name = "end_time")
    val endTime: Long?, // Null if interruption is ongoing

    val type: String, // "BACKGROUND", "SCREEN_LOCK", "APP_SWITCH"

    val duration: Long, // milliseconds

    @ColumnInfo(name = "distraction_package")
    val distractionPackage: String? = null
)
