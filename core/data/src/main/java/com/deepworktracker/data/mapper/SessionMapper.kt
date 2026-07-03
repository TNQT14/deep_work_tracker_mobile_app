package com.deepworktracker.data.mapper

import com.deepworktracker.common.datetime.toEpochMilliseconds
import com.deepworktracker.data.database.entity.FocusSessionEntity
import com.deepworktracker.domain.model.FocusSession
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SessionMapper {
    
    fun toDomain(entity: FocusSessionEntity): FocusSession {
        return FocusSession(
            id = entity.id,
            goal = entity.goal,
            category = entity.category,
            startTime = Instant.fromEpochMilliseconds(entity.startTime),
            endTime = entity.endTime?.let { Instant.fromEpochMilliseconds(it) },
            totalDuration = entity.totalDuration,
            focusedDuration = entity.focusedDuration,
            tag = entity.tag,
            note = entity.note,
            interruptions = emptyList(),
            todoId = entity.todoId,
            focusMinutes = entity.focusMinutes,
            breakMinutes = entity.breakMinutes,
            repeat = entity.repeat,
            alertMode = entity.alertMode,
            actualFocusedMinutes = entity.actualFocusedMinutes,
            cycles = entity.cycles,
        )
    }

    fun toEntity(domain: FocusSession): FocusSessionEntity {
        val date = domain.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
            .date.toString()

        return FocusSessionEntity(
            id = domain.id,
            goal = domain.goal,
            category = domain.category,
            startTime = domain.startTime.toEpochMilliseconds(),
            endTime = domain.endTime?.toEpochMilliseconds(),
            totalDuration = domain.totalDuration,
            focusedDuration = domain.focusedDuration,
            tag = domain.tag,
            note = domain.note,
            date = date,
            todoId = domain.todoId,
            focusMinutes = domain.focusMinutes,
            breakMinutes = domain.breakMinutes,
            repeat = domain.repeat,
            alertMode = domain.alertMode,
            actualFocusedMinutes = domain.actualFocusedMinutes,
            cycles = domain.cycles,
        )
    }
}
