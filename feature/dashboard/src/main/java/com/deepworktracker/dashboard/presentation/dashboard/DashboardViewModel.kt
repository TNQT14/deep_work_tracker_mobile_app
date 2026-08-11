package com.deepworktracker.dashboard.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.common.result.Result
import com.deepworktracker.dashboard.domain.usecase.GetFocusAnalyticsUseCase
import com.deepworktracker.dashboard.domain.usecase.GetRecentSessionsUseCase
import com.deepworktracker.dashboard.domain.usecase.GetStreakUseCase
import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.repository.InsightRepository
import com.deepworktracker.domain.repository.SessionRepository
import com.deepworktracker.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getRecentSessionsUseCase: GetRecentSessionsUseCase,
    private val getFocusAnalyticsUseCase: GetFocusAnalyticsUseCase,
    private val getStreakUseCase: GetStreakUseCase,

    private val insightRepository: InsightRepository,
    private val sessionRepository: SessionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()


    init {
        observeInsights()
        // observeStreak() emits the current daily goal immediately, which loads the
        // streak once — loadDashboardData() must not load it a second time.
        observeStreak()
        loadDashboardData()
    }

    /**
     * [ViewModel] [UDF: Room Flow -> state (no user event involved)]
     * Input: none (reads insightRepository)
     * Process: subscribes to InsightRepository.getRecentInsights(limit=5), a Room Flow
     *          that automatically re-emits whenever the `insights` table changes
     *          (a new row inserted by GenerateInsightsWorker). `collect` never
     *          completes — this coroutine lives for as long as the ViewModel does
     *          (viewModelScope).
     * Output: uiState.insights replaced with the latest emission on every change.
     */
    fun observeInsights() {
        viewModelScope.launch {
            insightRepository.getRecentInsights(limit = 5).collect { insights ->
                _uiState.update {
                    it.copy(insights = insights)
                }
            }
        }
    }

    private fun observeStreak() {
        viewModelScope.launch {
            userPreferencesRepository.observePreferences()
                .map { it.dailyGoalMinutes }
                .distinctUntilChanged()
                .collect { goal ->
                    _uiState.update { it.copy(dailyGoalMinutes = goal) }
                    refreshStreak()
                }
        }
    }

    private fun loadStreak() {
        viewModelScope.launch { refreshStreak() }
    }

    /**
     * Loads every dashboard section in one coroutine so `isLoading` flips to false exactly
     * once, after the last section has landed. The streak is not loaded here — observeStreak()
     * owns it and re-loads whenever the daily goal changes.
     */
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val sessionsResult = getRecentSessionsUseCase()) {
                is Result.Success ->
                    _uiState.update { it.copy(recentSessions = sessionsResult.data) }

                is Result.Error ->
                    _uiState.update { it.copy(error = sessionsResult.exception) }
            }

            runCatching { sessionRepository.getAllSessions() }
                .onSuccess { sessions -> _uiState.update { it.copy(allSessions = sessions) } }
                .onFailure { e -> _uiState.update { it.copy(error = e) } }

            refreshAnalytics(_uiState.value.selectedPeriod)

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun refresh() {
        loadDashboardData()
        loadStreak()
    }

    fun onPeriodSelected(period: AnalyticsPeriod) {
        if (_uiState.value.selectedPeriod == period) return
        _uiState.update { it.copy(selectedPeriod = period) }
        viewModelScope.launch { refreshAnalytics(period) }
    }

    private suspend fun refreshAnalytics(period: AnalyticsPeriod) {
        when (val result = getFocusAnalyticsUseCase(period)) {
            is Result.Success -> _uiState.update { it.copy(focusAnalytics = result.data) }
            is Result.Error -> _uiState.update { it.copy(error = result.exception) }
        }
    }

    private suspend fun refreshStreak() {
        when (val result = getStreakUseCase()) {
            is Result.Success -> _uiState.update { it.copy(streak = result.data) }
            is Result.Error -> _uiState.update { it.copy(error = result.exception) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

}
