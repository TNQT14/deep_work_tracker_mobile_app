package com.deepworktracker.dashboard.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.common.result.Result
import com.deepworktracker.dashboard.domain.usecase.GetAllSessionUseCase
import com.deepworktracker.dashboard.domain.usecase.GetFocusAnalyticsUseCase
import com.deepworktracker.dashboard.domain.usecase.GetRecentSessionsUseCase
import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.model.AlertMode
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.Interruption
import com.deepworktracker.domain.model.InterruptionType
import com.deepworktracker.domain.repository.InsightRepository
import com.deepworktracker.domain.repository.InterruptionRepository
import com.deepworktracker.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getRecentSessionsUseCase: GetRecentSessionsUseCase,
    private val getAllSessionUseCase: GetAllSessionUseCase,
    private val getFocusAnalyticsUseCase: GetFocusAnalyticsUseCase,
    private val insightRepository: InsightRepository,
    // TEMPORARY DEBUG (M3.3b device verification) — remove together with
    // debugSeedTestData()/seedSession() and the button in DashboardScreen once verified.
    private val sessionRepository: SessionRepository,
    private val interruptionRepository: InterruptionRepository,
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
     *          (a new row inserted by GenerateInsightsWorker). `collect` never
     *          completes — this coroutine lives for as long as the ViewModel does
     *          (viewModelScope).
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

    // ============================================================
    // TEMPORARY DEBUG (M3.3b) — seed session/interruption data only.
    // Does NOT call GenerateInsightsUseCase — the real worker
    // (GenerateInsightsWorker, triggered via Background Task Inspector
    // "Run Now") must be what generates the insights, so this actually
    // verifies the Worker + Hilt + Room + UI pipeline end-to-end.
    // DELETE this block + the button in DashboardScreen after verifying.
    // ============================================================
    fun debugSeedTestData() {
        viewModelScope.launch {
            val now = Clock.System.now()

            val thisWeekIds = mutableListOf<String>()
            repeat(3) { i -> thisWeekIds += seedSession(now, daysAgo = i, focusMin = 25, focusRatio = 0.3f) }
            repeat(3) { i -> thisWeekIds += seedSession(now, daysAgo = i, focusMin = 50, focusRatio = 0.6f) }
            repeat(3) { i -> seedSession(now, daysAgo = 10 + i, focusMin = 40, focusRatio = 0.9f) }

            val counts = listOf(1, 1, 1, 5)
            counts.forEachIndexed { dayIndex, count ->
                repeat(count) {
                    interruptionRepository.saveInterruption(
                        Interruption(
                            id = UUID.randomUUID().toString(),
                            sessionId = thisWeekIds[dayIndex % thisWeekIds.size],
                            startTime = now - dayIndex.days,
                            endTime = (now - dayIndex.days).plus(2, DateTimeUnit.MINUTE),
                            type = InterruptionType.APP_SWITCH,
                            duration = 120_000L,
                        )
                    )
                }
            }
        }
    }

    private suspend fun seedSession(
        now: Instant,
        daysAgo: Int,
        focusMin: Int,
        focusRatio: Float,
    ): String {
        val id = UUID.randomUUID().toString()
        val start = now - daysAgo.days
        val totalMs = focusMin * 60_000L
        sessionRepository.saveSession(
            FocusSession(
                id = id,
                goal = "Debug seed",
                category = "Test",
                startTime = start,
                endTime = start.plus(focusMin.toLong(), DateTimeUnit.MINUTE),
                totalDuration = totalMs,
                focusedDuration = (totalMs * focusRatio).toLong(),
                tag = null,
                note = null,
                focusMinutes = focusMin,
                alertMode = AlertMode.NOTIFY,
            )
        )
        return id
    }
}