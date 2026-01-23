package com.deepworktracker.data.mapper

import com.deepworktracker.data.database.entity.DailyStatsEntity
import com.deepworktracker.domain.model.DailyStats
import kotlinx.datetime.LocalDate

class StatsMapper {
    
    fun toDomain(entity: DailyStatsEntity): DailyStats {
        return DailyStats(
            date = LocalDate.parse(entity.date),
            totalFocusTime = entity.totalFocusTime,
            sessionCount = entity.sessionCount,
            interruptionCount = entity.interruptionCount,
            averageSessionDuration = entity.averageSessionDuration,
            bestFocusHour = entity.bestFocusHour
        )
    }
    
    fun toEntity(domain: DailyStats): DailyStatsEntity {
        return DailyStatsEntity(
            date = domain.date.toString(),
            totalFocusTime = domain.totalFocusTime,
            sessionCount = domain.sessionCount,
            interruptionCount = domain.interruptionCount,
            averageSessionDuration = domain.averageSessionDuration,
            bestFocusHour = domain.bestFocusHour
        )
    }
}
