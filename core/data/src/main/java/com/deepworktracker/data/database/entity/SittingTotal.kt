package com.deepworktracker.data.database.entity

import androidx.room.ColumnInfo

data class SittingTotal(
    @ColumnInfo(name = "sittingId")
    val sittingId: String,
    @ColumnInfo(name = "totalMs")
    val totalMs: Long,
    @ColumnInfo(name = "startMs")
    val startMs: Long,
    @ColumnInfo(name = "endMs")
    val endMs: Long,
)
