package com.deepworktracker.domain.model

import com.deepworktracker.domain.model.preferences.AccessibilityPreferences
import kotlinx.datetime.Instant

enum class PreferenceScope {
    DEVICE,
    ACCOUNT;
    companion object {
        fun fromStorage(value: String?): PreferenceScope =
            entries.find { it.name == value } ?: DEVICE
    }
}

data class UserPreferences(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val language: LanguagePreference = LanguagePreference.System,
    val accessibilityPreferences: AccessibilityPreferences = AccessibilityPreferences(),
    val scope: PreferenceScope = PreferenceScope.DEVICE,
    val updatedAt: Instant? = null,
    val syncVersion: Long = 0L,
    val hasExplicitThemeChoice: Boolean = false,
    val hasExplicitLanguageChoice: Boolean = false
) {
    companion object {
        val DEFAULT = UserPreferences(
        )
    }
}