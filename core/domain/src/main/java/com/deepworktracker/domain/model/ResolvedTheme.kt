package com.deepworktracker.domain.model.preferences

import com.deepworktracker.domain.model.ThemePreference

data class ResolvedTheme(
    val isDark: Boolean,
    val userChoice: ThemePreference,
    val followsSystem: Boolean,
)