package com.deepworktracker.data.mapper

import com.deepworktracker.common.datetime.toEpochMilliseconds
import com.deepworktracker.data.database.entity.InterruptionEntity
import com.deepworktracker.domain.model.Interruption
import com.deepworktracker.domain.model.InterruptionType
import kotlinx.datetime.Instant

class InterruptionMapper {
    
    fun toDomain(entity: InterruptionEntity): Interruption {
        return Interruption(
            id = entity.id,
            sessionId = entity.sessionId,
            startTime = Instant.fromEpochMilliseconds(entity.startTime),
            endTime = entity.endTime?.let { Instant.fromEpochMilliseconds(it) },
            type = InterruptionType.valueOf(entity.type),
            duration = entity.duration
        )
    }
    
    fun toEntity(domain: Interruption): InterruptionEntity {
        return InterruptionEntity(
            id = domain.id,
            sessionId = domain.sessionId,
            startTime = domain.startTime.toEpochMilliseconds(),
            endTime = domain.endTime?.toEpochMilliseconds(),
            type = domain.type.name,
            duration = domain.duration
        )
    }
}
