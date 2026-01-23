package com.deepworktracker.domain.model

import kotlinx.datetime.Instant

data class FocusSession(
    val id: String,
    val goal: String,
    val startTime: Instant,
    val endTime: Instant?,
    val totalDuration: Long, // milliseconds
    val focusedDuration: Long, // milliseconds (total - interruptions)
    val tag: String?,
    val note: String?,
    val interruptions: List<Interruption> = emptyList()
) {
    val isActive: Boolean
        get() = endTime == null
}
