package com.deepworktracker.domain.model

data class FocusConfig(
    val focusMinutes: Int,
    val breakMinutes: Int,
    val repeat: Boolean,
    val alertMode: AlertMode,
)
