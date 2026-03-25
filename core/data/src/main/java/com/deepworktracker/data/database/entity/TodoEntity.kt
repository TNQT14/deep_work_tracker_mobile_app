package com.deepworktracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.deepworktracker.domain.model.TodoStatus

@Entity(
    tableName = "todos",
    indices = [
        Index(value = ["goal"]),
        Index(value = ["status"]),
        Index(value = ["created_at"]),
        Index(value = ["due_at"]),
    ]
)

data class TodoEntity(
    @PrimaryKey val id: String,

    val goal: String,
    val title: String,
    val description: String,
    val status: TodoStatus,
    val priority: Int,

    @ColumnInfo(name = "due_at")
    val dueAt: Long?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "completed_at")
    val completedAt: Long?
)
