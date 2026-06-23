package com.deepworktracker.profile.presentation.setting_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingRoute(
    onBack:()-> Unit,
    viewModel: SettingViewModel = hiltViewModel()
    ){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingScreen(
        uiState = uiState,
        onBack = onBack,
        onThemeSelected = viewModel::onThemeSelected,
        onLanguageTagSelected  = viewModel::onLanguageTagSelected,
        onClearError = viewModel::clearError,
    )
}