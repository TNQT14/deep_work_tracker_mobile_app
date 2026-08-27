package com.deepworktracker.dashboard.presentation.day_history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.analytics.FocusScoreCalculator
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class DayHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val date: LocalDate = LocalDate.parse(savedStateHandle.get<String>(ARG_DATE).orEmpty())

    private val _uiState = MutableStateFlow(DayHistoryUiState(date = date))
    val uiState: StateFlow<DayHistoryUiState> = _uiState.asStateFlow()

    init {
        observeSessions()
    }

    private fun observeSessions() {
        viewModelScope.launch {
            try {
                sessionRepository.getSessionsByDate(date).collect { all ->
                    val completed = all.filter { s -> !s.isActive }.sortedBy { s -> s.startTime }
                    _uiState.update {
                        it.copy(
                            groups = groupSession(all),
                            stats = computeStats(completed),
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load sessions"
                    )
                }
            }
        }
    }

    private fun computeStats(sessions: List<FocusSession>): DayStats {
        val focusedMs = sessions.sumOf { it.focusedDuration }.coerceAtLeast(0L)
        val interruptedMs = FocusScoreCalculator.interruptedMs(sessions)
        return DayStats(
            sessionCount = sessions.size,
            totalMs = focusedMs + interruptedMs,
            interruptedMs = interruptedMs,
            focusScore = FocusScoreCalculator.score(sessions),
        )
    }

    private fun groupSession(session: List<FocusSession>): List<SessionGroup> =
        session.groupBy { it.todoId ?: it.goal }
            .values
            .map { it.sortedBy { s -> s.startTime } }
            .sortedBy { it.first().startTime }

    companion object {
        const val ARG_DATE = "date"
    }
}