package com.deepworktracker.domain.model

import kotlinx.datetime.Instant

data class Interruption(
    val id: String,
    val sessionId: String,
    val startTime: Instant,
    val endTime: Instant?,
    val type: InterruptionType,
    val duration: Long,
    val distractionPackage: String? = null
) {
    val isActive: Boolean
        get() = endTime == null
}

enum class InterruptionType {
    BACKGROUND,
    SCREEN_LOCK,
    APP_SWITCH
}
