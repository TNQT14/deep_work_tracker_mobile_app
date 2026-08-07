package com.deepworktracker.dashboard.presentation.dashboard

import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.analytics.FocusAnalytics
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.Insight

data class DashboardUiState(
    val recentSessions: List<FocusSession> = emptyList(),
    val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.WEEK,
    val focusAnalytics: FocusAnalytics? = null,
    val allSessions: List<FocusSession> = emptyList(),
    val insights: List<Insight> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null
)