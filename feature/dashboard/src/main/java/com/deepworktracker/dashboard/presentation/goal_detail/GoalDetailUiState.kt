package com.deepworktracker.dashboard.presentation.goal_detail

import com.deepworktracker.domain.model.FocusSession
import kotlinx.datetime.LocalDate

data class GoalDetailUiState(
    val sessions: List<FocusSession> = emptyList(),
    val totalDuration: Long = 0L,
    val isLoading: Boolean = false,
    val error: String? = null,
    val chartByHour: List<Long> = emptyList(),
    val chartByDay: List<Pair<LocalDate, Long>> = emptyList(),
    val chartByWeek: List<Pair<String, Long>> = emptyList(),
    val chartByMonth: List<Pair<String, Long>> = emptyList(),
    val chartByYear: List<Pair<Int, Long>> = emptyList()
)