package com.deepworktracker.data.preferences

import androidx.datastore.preferences.core.Preferences
import com.deepworktracker.domain.model.LanguagePreference
import com.deepworktracker.domain.model.PreferenceScope
import com.deepworktracker.domain.model.ThemePreference
import com.deepworktracker.domain.model.UserPreferences
import com.deepworktracker.domain.model.preferences.AccessibilityPreferences
import kotlinx.datetime.Instant

object UserPreferencesMapper {

    private const val NO_TIMESTAMP = 0L

    fun toDomain(preferences: Preferences): UserPreferences {
        val updatedAtMillis = preferences[PreferencesKeys.UPDATED_AT] ?: NO_TIMESTAMP
        return UserPreferences(
            theme = ThemePreference.fromStorage(preferences[PreferencesKeys.THEME]),
            language = LanguagePreference.fromStorage(preferences[PreferencesKeys.LANGUAGE]),
            accessibilityPreferences = AccessibilityPreferences(
                respectSystemFontScale = preferences[PreferencesKeys.RESPECT_SYSTEM_FONT_SCALE] ?: true,
                respectReducedMotion = preferences[PreferencesKeys.RESPECT_REDUCED_MOTION] ?: true,
                forceHighContrast = preferences[PreferencesKeys.FORCE_HIGH_CONTRAST] ?: false,
            ),
            dailyGoalMinutes = preferences[PreferencesKeys.DAILY_GOAL_MINUTES] ?: 0,
            scope = PreferenceScope.fromStorage(preferences[PreferencesKeys.SCOPE]),
            updatedAt = updatedAtMillis.takeIf { it > NO_TIMESTAMP }?.let(Instant::fromEpochMilliseconds),
            syncVersion = preferences[PreferencesKeys.SYNC_VERSION] ?: 0L,
            hasExplicitThemeChoice = preferences[PreferencesKeys.HAS_EXPLICIT_THEME] ?: false,
            hasExplicitLanguageChoice = preferences[PreferencesKeys.HAS_EXPLICIT_LANGUAGE] ?: false,
        )
    }

    fun toPreferences(domain: UserPreferences): Preferences {
        return androidx.datastore.preferences.core.preferencesOf(
            PreferencesKeys.THEME to domain.theme.name,
            PreferencesKeys.LANGUAGE to domain.language.storageKey,
            PreferencesKeys.HAS_EXPLICIT_THEME to domain.hasExplicitThemeChoice,
            PreferencesKeys.HAS_EXPLICIT_LANGUAGE to domain.hasExplicitLanguageChoice,
            PreferencesKeys.UPDATED_AT to (domain.updatedAt?.toEpochMilliseconds() ?: NO_TIMESTAMP),
            PreferencesKeys.SYNC_VERSION to domain.syncVersion,
            PreferencesKeys.DAILY_GOAL_MINUTES to domain.dailyGoalMinutes,
            PreferencesKeys.SCOPE to domain.scope.name,
            PreferencesKeys.RESPECT_SYSTEM_FONT_SCALE to domain.accessibilityPreferences.respectSystemFontScale,
            PreferencesKeys.RESPECT_REDUCED_MOTION to domain.accessibilityPreferences.respectReducedMotion,
            PreferencesKeys.FORCE_HIGH_CONTRAST to domain.accessibilityPreferences.forceHighContrast,
        )
    }
}