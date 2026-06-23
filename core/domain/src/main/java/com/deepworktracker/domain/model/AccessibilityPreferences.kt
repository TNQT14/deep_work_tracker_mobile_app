package com.deepworktracker.domain.model.preferences
data class AccessibilityPreferences(
    val respectSystemFontScale: Boolean = true,
    val respectReducedMotion: Boolean = true,
    val forceHighContrast: Boolean = false,
)
