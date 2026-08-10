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

    /**
     * [UiState]
     * Type: List<Insight>
     * Sample: [Insight(id="i1", type=BEST_TIME_WINDOW, data={"hours":"9,10"}, confidence=null)]
     * Populated reactively by DashboardViewModel.observeInsights() collecting
     * InsightRepository.getRecentInsights(limit=5) — never written to directly by any
     * user action; dismissing an insight goes through the repository, and this field
     * updates only because the underlying Room Flow re-emits.
     */
    val insights: List<Insight> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null
)