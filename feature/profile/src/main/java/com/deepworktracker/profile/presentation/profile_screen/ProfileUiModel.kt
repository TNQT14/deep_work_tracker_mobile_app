package com.deepworktracker.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Load all sessions to calculate stats
                val allSessions = sessionRepository.getAllSessions()

                val totalSessions = allSessions.size
                val totalFocusTime = allSessions.sumOf { it.totalDuration }

                _uiState.update {
                    it.copy(
                        userName = "User", // Default, sẽ update sau
                        totalSessions = totalSessions,
                        totalFocusTime = totalFocusTime,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refresh() {
        loadProfileData()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}