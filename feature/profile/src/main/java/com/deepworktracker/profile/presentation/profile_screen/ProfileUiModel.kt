package com.deepworktracker.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.data.remote.auth.AuthSessionRepository
import com.deepworktracker.data.remote.network.NetworkResult
import com.deepworktracker.data.repository.AuthRepository
import com.deepworktracker.data.remote.token.TokenStore
import com.deepworktracker.data.repository.UserRepository
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
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore,
    private val authSessionRepository: AuthSessionRepository,
    private val userRepository: UserRepository
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
                val allSessions = sessionRepository.getAllSessions()

                val totalSessions = allSessions.size
                val totalFocusTime = allSessions.sumOf { it.totalDuration }

                _uiState.update {
                    it.copy(
                        totalSessions = totalSessions,
                        totalFocusTime = totalFocusTime,
                        isLoading = false,
                    )
                }

                when (val result = userRepository.getMe()) {
                    is NetworkResult.Success -> _uiState.update {
                        it.copy(userName = result.data.fullName, email = result.data.email)
                    }
                    else -> Unit // Keep stats loaded even if profile fetch fails; stale name stays as-is.
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

    fun logout() {
        viewModelScope.launch {
            val accessToken = tokenStore.getAccessToken()
            if (accessToken.isNullOrBlank()) {
                authSessionRepository.logout()
                _uiState.update { it.copy(logoutState = LogoutState.Success) }
                return@launch
            }

            _uiState.update { it.copy(logoutState = LogoutState.Loading) }

            when (authRepository.logout(accessToken)) {
                is NetworkResult.Success -> {
                    authSessionRepository.logout()
                    _uiState.update { it.copy(logoutState = LogoutState.Success) }
                }

                else -> {
                    // Offline / 401 / 5xx: vẫn xóa token local (best-effort logout)
                    authSessionRepository.logout()
                    _uiState.update { it.copy(logoutState = LogoutState.Success) }
                }
            }
        }
    }

    fun consumeLogoutSuccess() {
        _uiState.update { it.copy(logoutState = LogoutState.Idle) }
    }

    /**
     * Input: fullName (required), password (blank = keep current)
     * Process: PUT /users/me via UserRepository — mirrors backend "empty string = no change"
     * Output: editProfileState transitions Loading -> Success (userName/email refreshed) | Error
     */
    fun updateProfile(fullName: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(editProfileState = EditProfileState.Loading) }
            when (val result = userRepository.updateMe(fullName, password)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(
                        userName = result.data.fullName,
                        email = result.data.email,
                        editProfileState = EditProfileState.Success,
                    )
                }
                else -> _uiState.update {
                    it.copy(editProfileState = EditProfileState.Error(mapNetworkErrorMessage(result)))
                }
            }
        }
    }

    private fun mapNetworkErrorMessage(result: NetworkResult<*>): String = when (result) {
        is NetworkResult.HttpError -> result.message ?: "HTTP ${result.code}"
        is NetworkResult.Unauthorized -> "Unauthorized"
        is NetworkResult.NotFound -> "Not found"
        is NetworkResult.ServerError -> "Server error"
        is NetworkResult.NoInternet -> "No internet connection"
        is NetworkResult.ParseError -> "Could not read server response"
        is NetworkResult.NetworkError -> result.message ?: "Network error"
        is NetworkResult.Success -> "Unexpected success"
    }


    fun consumeEditProfileSuccess() {
        _uiState.update { it.copy(editProfileState = EditProfileState.Idle) }
    }

    fun clearEditProfileError() {
        _uiState.update { it.copy(editProfileState = EditProfileState.Idle) }
    }


}