package com.deepworktracker.data.repository

import com.deepworktracker.data.preferences.PreferencesLocalDataSource
import com.deepworktracker.domain.model.LanguagePreference
import com.deepworktracker.domain.model.ThemePreference
import com.deepworktracker.domain.model.UserPreferences
import com.deepworktracker.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val localDataSource: PreferencesLocalDataSource,
): UserPreferencesRepository
{
    override fun observePreferences(): Flow<UserPreferences> = localDataSource.observePreferences()

    override suspend fun getPreferences(): UserPreferences = localDataSource.getPreferences()

    override suspend fun setTheme(theme: ThemePreference): Result<Unit> = localDataSource.updatePreferences {
        current -> current.copy(
            theme = theme,
            hasExplicitThemeChoice = true,
            updatedAt = Clock.System.now()
        )
    }

    override suspend fun setLanguage(language: LanguagePreference): Result<Unit> = localDataSource.updatePreferences {
        current -> current.copy(
            language = language,
            hasExplicitLanguageChoice = true,
            updatedAt = Clock.System.now()
        )
    }

    override suspend fun resetToDefaults(): Result<Unit> =
        localDataSource.savePreferences(UserPreferences.DEFAULT)

}