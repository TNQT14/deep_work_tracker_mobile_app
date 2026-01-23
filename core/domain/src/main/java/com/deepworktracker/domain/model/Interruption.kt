package com.deepworktracker.domain.model

import kotlinx.datetime.Instant

data class Interruption(
    val id: String,
    val sessionId: String,
    val startTime: Instant,
    val endTime: Instant?,
    val type: InterruptionType,
    val duration: Long // milliseconds
) {
    val isActive: Boolean
        get() = endTime == null
}

enum class InterruptionType {
    BACKGROUND,      // App went to background
    SCREEN_LOCK,     // Screen was locked
    APP_SWITCH       // User switched to another app
}
