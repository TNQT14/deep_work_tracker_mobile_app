package com.deepworktracker.profile.presentation.setting_screen

import com.deepworktracker.domain.model.LanguagePreference
import com.deepworktracker.domain.model.ThemePreference

data class SettingUiState(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val language: LanguagePreference = LanguagePreference.System,
    val isSaving: Boolean = false,
    val errorMsg: String? = null
)
