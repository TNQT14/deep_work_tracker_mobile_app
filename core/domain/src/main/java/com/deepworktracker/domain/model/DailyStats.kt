package com.deepworktracker.domain.model

import kotlinx.datetime.LocalDate

data class DailyStats(
    val date: LocalDate,
    val totalFocusTime: Long, // milliseconds
    val sessionCount: Int,
    val interruptionCount: Int,
    val averageSessionDuration: Long, // milliseconds
    val bestFocusHour: Int? // 0-23, hour with most focus time
)
