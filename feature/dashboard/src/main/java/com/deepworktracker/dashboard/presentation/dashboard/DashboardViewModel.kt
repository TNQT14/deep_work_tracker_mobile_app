package com.deepworktracker.dashboard.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.common.result.Result
import com.deepworktracker.dashboard.domain.usecase.GenerateInsightsUseCase
import com.deepworktracker.dashboard.domain.usecase.GetAllSessionUseCase
import com.deepworktracker.dashboard.domain.usecase.GetFocusAnalyticsUseCase
import com.deepworktracker.dashboard.domain.usecase.GetRecentSessionsUseCase
import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.repository.InsightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getRecentSessionsUseCase: GetRecentSessionsUseCase,
    private val getAllSessionUseCase: GetAllSessionUseCase,
    private val getFocusAnalyticsUseCase: GetFocusAnalyticsUseCase,
    private val insightRepository: InsightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()



    init {
        loadDashboardData()
        observeInsights()
    }

    /**
     * [ViewModel] [UDF: Room Flow -> state (no user event involved)]
     * Input: none (reads insightRepository)
     * Process: subscribes to InsightRepository.getRecentInsights(limit=5), a Room Flow
     *          that automatically re-emits whenever the `insights` table changes
     *          (a new row inserted by GenerateInsightsWorker, or is_dismissed flipped
     *          by onDissmissInsight below). `collect` never completes — this coroutine
     *          lives for as long as the ViewModel does (viewModelScope).
     * Output: uiState.insights replaced with the latest emission on every change.
     */
    fun observeInsights(){
        viewModelScope.launch {
            insightRepository.getRecentInsights(limit = 5).collect {
                insights -> _uiState.update {
                    it.copy(insights = insights)
                }
            }
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load recent sessions
            when (val sessionsResult = getRecentSessionsUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            recentSessions = sessionsResult.data,
                            isLoading = false
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = sessionsResult.exception,
                            isLoading = false
                        )
                    }
                }
            }

            when (val allSessionResult = getAllSessionUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            allSessions = allSessionResult.data,
                            isLoading = false
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = allSessionResult.exception,
                            isLoading = false
                        )
                    }
                }
            }
        }
        loadAnalytics(_uiState.value.selectedPeriod)
    }

    fun refresh() {
        loadDashboardData()
    }

    fun onPeriodSelected(period: AnalyticsPeriod) {
        if (_uiState.value.selectedPeriod == period) return
        _uiState.update { it.copy(selectedPeriod = period) }
        loadAnalytics(period)
    }

    /**
     * [ViewModel] [UDF: event (swipe) -> repository, state updates itself via Flow]
     * Input: id — Insight.id, e.g. "a1b2c3..."
     * Process: marks the row is_dismissed=1 in Room. Deliberately does NOT call
     *          _uiState.update here — observeInsights()'s Flow collection is the
     *          single source of truth for uiState.insights, so mutating it directly
     *          here would risk it getting overwritten by a stale emission.
     * Output: uiState.insights shrinks by one item once the Flow re-emits.
     */
    fun onDissmissInsight(id: String){
        viewModelScope.launch {
            insightRepository.dismissInsight(id)
        }
    }

    private fun loadAnalytics(period: AnalyticsPeriod) {
        viewModelScope.launch {
            when (val result = getFocusAnalyticsUseCase(period)) {
                is Result.Success -> _uiState.update { it.copy(focusAnalytics = result.data) }
                is Result.Error -> _uiState.update { it.copy(error = result.exception) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}