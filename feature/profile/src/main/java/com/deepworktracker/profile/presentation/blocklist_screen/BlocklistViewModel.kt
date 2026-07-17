package com.deepworktracker.profile.presentation.blocklist_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.repository.FocusShieldRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlocklistViewModel @Inject constructor(
    private val installedAppProvider: InstalledAppProvider,
    private val focusShieldRepository: FocusShieldRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlocklistUiState())
    val uiState: StateFlow<BlocklistUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val apps = installedAppProvider.loadLaunchableApps()
            _uiState.update {
                it.copy(apps = apps, isLoading = false)
            }
        }

        viewModelScope.launch {
            focusShieldRepository.observeConfig().collect { config ->
                _uiState.update { it.copy(blocklist = config.blocklist) }
            }
        }
    }

    fun onQueryChange(query: String) = _uiState.update { it.copy(query = query) }

    fun clearError() = _uiState.update { it.copy(errorMsg = null) }

    fun onToggle(packageName: String, checked: Boolean) {
        viewModelScope.launch {
            val current = focusShieldRepository.getConfig().blocklist
            val updated = if (checked) current + packageName else current - packageName
            focusShieldRepository.setBlocklist(updated)
                .onFailure { error ->
                    _uiState.update { it.copy(errorMsg = error.message ?: "Failed to save") }
                }
        }
    }

}