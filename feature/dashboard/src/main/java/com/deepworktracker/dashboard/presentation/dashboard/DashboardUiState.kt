package com.deepworktracker.dashboard.presentation.dashboard

import com.deepworktracker.dashboard.domain.usecase.TodayStats
import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.analytics.FocusAnalytics
import com.deepworktracker.domain.model.FocusSession

data class DashboardUiState(
    val todayStats: TodayStats? = null,
    val recentSessions: List<FocusSession> = emptyList(),
    val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.WEEK,
    val focusAnalytics: FocusAnalytics? = null,
    val allSessions: List<FocusSession> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null
)