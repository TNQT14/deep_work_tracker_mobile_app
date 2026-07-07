package com.deepworktracker.profile.presentation

data class ProfileUiState(
    val userName: String = "",
    val email: String = "",
    val totalSessions: Int = 0,
    val totalFocusTime: Long = 0L, // milliseconds
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val logoutState: LogoutState = LogoutState.Idle,
    val editProfileState: EditProfileState = EditProfileState.Idle,
)

sealed interface LogoutState {
    data object Idle : LogoutState
    data object Loading : LogoutState
    data object Success : LogoutState
}

sealed interface EditProfileState {
    data object Idle : EditProfileState
    data object Loading : EditProfileState
    data object Success : EditProfileState
    data class Error(val message: String) : EditProfileState
}