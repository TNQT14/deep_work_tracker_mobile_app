package com.deepworktracker.data.mapper

import com.deepworktracker.common.datetime.toEpochMilliseconds
import com.deepworktracker.data.database.entity.TodoEntity
import com.deepworktracker.domain.model.Todo
import kotlinx.datetime.Instant

class TodoMapper {
    fun toDomain(entity: TodoEntity): Todo {
        return Todo(
            id = entity.id,
            goal = entity.goal,
            title = entity.title,
            description = entity.description,
            status = entity.status,
            priority = entity.priority,
            dueAt = entity.dueAt?.let { Instant.fromEpochMilliseconds(it) },
            completedAt = entity.completedAt?.let { Instant.fromEpochMilliseconds(it) },
            createdAt = Instant.fromEpochMilliseconds(entity.createdAt),
            updatedAt = Instant.fromEpochMilliseconds(entity.updatedAt),
        )
    }

    fun toEntity(domain: Todo): TodoEntity {
        return TodoEntity(
            id = domain.id,
            goal = domain.goal,
            title = domain.title,
            description = domain.description,
            status = domain.status,
            priority = domain.priority,
            dueAt = domain.dueAt?.toEpochMilliseconds(),
            completedAt = domain.completedAt?.toEpochMilliseconds(),
            createdAt = domain.createdAt.toEpochMilliseconds(),
            updatedAt = domain.updatedAt.toEpochMilliseconds(),
        )
    }
}