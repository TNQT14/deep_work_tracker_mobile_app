package com.deepworktracker.data.database.entity

import androidx.room.ColumnInfo

data class DailyFocusTotal (
    @ColumnInfo(name = "date")
    val date:String,
    @ColumnInfo(name = "total_ms")
    val totalMs: Long,
)