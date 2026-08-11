package com.deepworktracker.profile.presentation.setting_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingRoute(
    onBack: () -> Unit,
    onNavigateToBlocklist: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingScreen(
        uiState = uiState,
        onBack = onBack,
        onNavigateToBlocklist = onNavigateToBlocklist,
        onThemeSelected = viewModel::onThemeSelected,
        onLanguageTagSelected = viewModel::onLanguageTagSelected,
        onDndToggle = viewModel::onDndToggle,
        onDailyGoalSelected = viewModel::onDailyGoalSelected,
        onClearError = viewModel::clearError,
    )
}