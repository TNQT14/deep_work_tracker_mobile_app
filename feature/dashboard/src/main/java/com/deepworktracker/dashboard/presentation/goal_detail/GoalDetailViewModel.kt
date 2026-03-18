package com.deepworktracker.dashboard.presentation.goal_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
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
                _uiState.value = _uiState.value.copy(isLoading = true,)

                val today = Clock.System.now().toLocalDateTime(TimeZone.Companion.currentSystemDefault()).date
                val onYearAgo = today.minus(1, DateTimeUnit.YEAR)

                val list = sessionRepository.getSessionsByDateRange(onYearAgo, today).first()
                val filtered = list.filter { it.goal == goal }.sortedByDescending { it.startTime }
                val total = filtered.sumOf { it.totalDuration }

                val zone = TimeZone.currentSystemDefault()

                val byHour = (0..23).map { hour ->
                    filtered
                        .filter { it.startTime.toLocalDateTime(zone).hour == hour }
                        .sumOf { it.totalDuration }
                }

                val byDayMap = filtered
                    .groupBy { it.startTime.toLocalDateTime(zone).date }
                    .mapValues { (_, sessions) -> sessions.sumOf { it.totalDuration } }
                val sortedDays = byDayMap.keys.sorted()
                val byDay = sortedDays.takeLast(30).map { d -> d to (byDayMap[d] ?: 0L) }

                val byMonthMap = filtered
                    .groupBy { it.startTime.toLocalDateTime(zone).let { "${it.year}-${it.monthNumber}" } }
                    .mapValues { (_, sessions) -> sessions.sumOf { it.totalDuration } }
                val monthOrder = sortedDays.map { "${it.year}-${it.month.number}" }.distinct().takeLast(12)
                val byMonth = monthOrder.map { key -> key to (byMonthMap[key] ?: 0L) }

                val byYearMap = filtered
                    .groupBy { it.startTime.toLocalDateTime(zone).year }
                    .mapValues { (_, sessions) -> sessions.sumOf { it.totalDuration } }
                val yearOrder = byYearMap.keys.sorted()
                val byYear = yearOrder.map { y -> y to (byYearMap[y] ?: 0L) }


                _uiState.value = GoalDetailUiState(
                    sessions = filtered,
                    totalDuration = total,
                    isLoading = false,
                    error = null,
                    chartByHour = byHour,
                    chartByDay = byDay,
                    chartByMonth = byMonth,
                    chartByYear = byYear,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage,)
            }
        }
    }
}