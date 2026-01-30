package com.deepworktracker.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class GoalDetailViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalDetailUiState())
    val uiState: StateFlow<GoalDetailUiState> = _uiState.asStateFlow()

    fun loadGoal(goal: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val thirtyDaysAgo = today.minus(30, DateTimeUnit.DAY)

                val list = sessionRepository.getSessionsByDateRange(thirtyDaysAgo, today).first()
                val filtered = list.filter { it.goal == goal }.sortedByDescending { it.startTime }
                val total = filtered.sumOf { it.totalDuration }

                _uiState.value = GoalDetailUiState(
                    sessions = filtered,
                    totalDuration = total,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }
}

