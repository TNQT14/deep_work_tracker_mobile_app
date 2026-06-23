package com.deepworktracker.preferences

import com.deepworktracker.domain.model.DeviceEnviroment
import com.deepworktracker.domain.model.ThemePreference
import com.deepworktracker.domain.model.UserPreferences
import com.deepworktracker.domain.model.preferences.ResolvedTheme
import com.deepworktracker.domain.preferences.ThemeResolver
import com.deepworktracker.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    val preferences: Flow<UserPreferences> = userPreferencesRepository.observePreferences()

    fun resolveTheme(
        preferences: UserPreferences,
        isSystemDark: Boolean
    ): ResolvedTheme = ThemeResolver.resolve(
        preference = preferences.theme,
        device = DeviceEnviroment(
            isSystemDark = isSystemDark,
            systemLocaleTag = "",
        ),
    )

    suspend fun setTheme(theme: ThemePreference): Result<Unit> =
        userPreferencesRepository.setTheme(theme)
}