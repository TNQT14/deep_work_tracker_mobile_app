package com.deepworktracker.domain.repository

import com.deepworktracker.domain.model.LanguagePreference
import com.deepworktracker.domain.model.ThemePreference
import com.deepworktracker.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observePreferences(): Flow<UserPreferences>
    suspend fun getPreferences(): UserPreferences
    suspend fun setTheme(theme: ThemePreference): Result<Unit>
    suspend fun setLanguage(language: LanguagePreference): Result<Unit>
    suspend fun resetToDefaults(): Result<Unit>
    suspend fun setDailyGoalMinutes(minutes: Int): Result<Unit>
}