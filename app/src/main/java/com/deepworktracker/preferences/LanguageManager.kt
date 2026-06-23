package com.deepworktracker.preferences

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.deepworktracker.domain.model.DeviceEnviroment
import com.deepworktracker.domain.model.LanguagePreference
import com.deepworktracker.domain.model.ResolvedLocale
import com.deepworktracker.domain.model.UserPreferences
import com.deepworktracker.domain.preferences.LocaleResolver
import com.deepworktracker.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    val preferences: Flow<UserPreferences> =
        userPreferencesRepository.observePreferences()

    fun resolveLocale(
        preferences: UserPreferences,
        systemLocaleTag: String,
    ): ResolvedLocale = LocaleResolver.resolve(
        preference = preferences.language,
        device = DeviceEnviroment(
            isSystemDark = false,
            systemLocaleTag = systemLocaleTag,
        ),
    )

    /**
     * Syncs the OS-level per-app locale via AppCompat. Not used for in-app
     * instant language switching — that is handled by [resolveLocale] +
     * ProvideAppLocale in DeepWorkAppRoot (Compose recomposition, no Activity
     * recreate). Kept for optional system-settings sync.
     */
    fun applyLanguagePreference(language: LanguagePreference) {
        val desired = when (language) {
            is LanguagePreference.System -> LocaleListCompat.getEmptyLocaleList()
            is LanguagePreference.Fixed -> LocaleListCompat.forLanguageTags(language.localeTag)
        }
        if (AppCompatDelegate.getApplicationLocales().toLanguageTags() != desired.toLanguageTags()) {
            AppCompatDelegate.setApplicationLocales(desired)
        }
    }

    suspend fun setLanguage(language: LanguagePreference): Result<Unit> =
        userPreferencesRepository.setLanguage(language)
}