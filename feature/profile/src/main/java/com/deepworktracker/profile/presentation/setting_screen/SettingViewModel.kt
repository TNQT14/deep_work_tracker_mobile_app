package com.deepworktracker.profile.presentation.setting_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deepworktracker.domain.model.LanguagePreference
import com.deepworktracker.domain.model.SupportedLocales
import com.deepworktracker.domain.model.ThemePreference
import com.deepworktracker.domain.repository.FocusShieldRepository
import com.deepworktracker.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val focusShieldRepository: FocusShieldRepository
) : ViewModel() {
    companion object {
        const val LANGUAGE_TAG_SYSTEM = "system"
    }

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.observePreferences().collect { prefs ->
                _uiState.update {
                    it.copy(
                        theme = prefs.theme,
                        language = prefs.language,
                        isSaving = false
                    )
                }
            }
        }
        viewModelScope.launch {
            focusShieldRepository.observeConfig().collect { config ->
                _uiState.update { it.copy(shieldDndEnabled = config.dndEnabled) }
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMsg = null)
        }
    }

    fun onThemeSelected(theme: ThemePreference) {
        if (_uiState.value.theme == theme) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMsg = null) }
            userPreferencesRepository.setTheme(theme)
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMsg = error.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

    fun onLanguageTagSelected(languageTag: String) {
        onLanguageSelected(
            when (languageTag) {
                LANGUAGE_TAG_SYSTEM -> LanguagePreference.System
                SupportedLocales.ENGLISH -> LanguagePreference.Fixed(SupportedLocales.ENGLISH)
                SupportedLocales.VIETNAMESE -> LanguagePreference.Fixed(SupportedLocales.VIETNAMESE)
                else -> LanguagePreference.System
            }
        )
    }

    fun onLanguageSelected(language: LanguagePreference) {
        if (_uiState.value.language == language) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMsg = null) }
            userPreferencesRepository.setLanguage(language)
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMsg = error.message ?: "Failed to save language",
                        )
                    }
                }
        }
    }

    fun onDndToggle(enabled: Boolean){
        if(_uiState.value.shieldDndEnabled == enabled) return
        viewModelScope.launch {
            focusShieldRepository.setDndEnabled(enabled).onFailure {
                _uiState.update {
                    it.copy(
                        errorMsg = it.errorMsg ?: "Failed to save DND setting"
                    )
                }
            }
        }
    }
}