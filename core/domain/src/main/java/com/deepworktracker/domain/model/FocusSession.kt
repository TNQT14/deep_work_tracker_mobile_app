package com.deepworktracker.domain.model

import kotlinx.datetime.Instant

data class FocusSession(
    val id: String,
    val goal: String,
    val category: String?,
    val startTime: Instant,
    val endTime: Instant?,
    val totalDuration: Long,
    val focusedDuration: Long,
    val tag: String?,
    val note: String?,
    val interruptions: List<Interruption> = emptyList(),
    val todoId: String? = null,
    val focusMinutes: Int = 25,
    val breakMinutes: Int = 5,
    val repeat: Boolean = false,
    val alertMode: AlertMode = AlertMode.NOTIFY,
    val actualFocusedMinutes: Int = 0,
    val cycles: Int = 0,
) {
    val isActive: Boolean
        get() = endTime == null
}
