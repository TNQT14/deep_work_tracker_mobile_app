package com.deepworktracker.domain.model

import kotlinx.datetime.Instant

enum class TodoStatus {
    TODO,
    IN_PROGRESS,
    PAUSED,
    DONE
}

data class Todo(
    val id: String,
    val title: String,
    val description: String,
    val status: TodoStatus,
    val goal: String,
    val priority: Int = 0,
    val dueAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val completedAt: Instant? = null,
    val estimatedMinutes: Int? = null,
)