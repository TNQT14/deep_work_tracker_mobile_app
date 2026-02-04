package com.deepworktracker.profile.presentation

data class ProfileUiState(
    val userName: String = "",
    val totalSessions: Int = 0,
    val totalFocusTime: Long = 0L, // milliseconds
    val isLoading: Boolean = false,
    val error: Throwable? = null
)