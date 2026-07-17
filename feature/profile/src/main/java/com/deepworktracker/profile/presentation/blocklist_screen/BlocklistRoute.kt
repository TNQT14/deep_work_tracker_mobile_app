package com.deepworktracker.profile.presentation.blocklist_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BlocklistRoute(
    onBack: ()-> Unit,
    viewModel: BlocklistViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BlocklistScreen(
        uiState = uiState,
        onBack = onBack,
        onQueryChange = viewModel::onQueryChange,
        onToggle = viewModel::onToggle,
        onClearError = viewModel::clearError,
    )
}