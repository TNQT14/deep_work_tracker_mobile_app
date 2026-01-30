package com.deepworktracker.dashboard.presentation

import com.deepworktracker.dashboard.domain.usecase.TodayStats
import com.deepworktracker.domain.model.FocusSession

data class DashboardUiState(
    val todayStats: TodayStats? = null,
    val recentSessions: List<FocusSession> = emptyList(),
    val allSessions: List<FocusSession> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null
)
