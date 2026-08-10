package com.deepworktracker.profile.presentation.setting_screen

import android.health.connect.datatypes.ExercisePerformanceGoal
import com.deepworktracker.domain.model.LanguagePreference
import com.deepworktracker.domain.model.ThemePreference

data class SettingUiState(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val language: LanguagePreference = LanguagePreference.System,
    val dailyGoalMinutes: Int = 0,
    val shieldDndEnabled: Boolean = false,
    val isSaving: Boolean = false,
    val errorMsg: String? = null
)
