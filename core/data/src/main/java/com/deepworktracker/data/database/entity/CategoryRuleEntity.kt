package com.deepworktracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "category_rules",
    indices = [
        Index(value = ["priority"])
    ]
)
data class CategoryRuleEntity(
    @PrimaryKey
    val id: String,
    val keyword: String?,
    @ColumnInfo(name = "start_hour")
    val startHour: Int?,
    @ColumnInfo(name = "end_hour")
    val endHour: Int?,
    val category: String,
    val tag: String?,
    val priority: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)

