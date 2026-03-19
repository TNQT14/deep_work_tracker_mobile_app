package com.deepworktracker.dashboard.presentation.category_detail

import com.deepworktracker.dashboard.presentation.goal_detail.GoalMetrics
import com.deepworktracker.domain.model.FocusSession
import kotlinx.datetime.LocalDate

data class CategoryDetailUiState(
    val sessions: List<FocusSession> = emptyList(),
    val totalDuration: Long = 0L,
    val isLoading: Boolean = false,
    val error: String? = null,
    val chartByHour: List<Long> = emptyList(),
    val chartByDay: List<Pair<LocalDate, Long>> = emptyList(),
    val chartByWeek: List<Pair<String, Long>> = emptyList(),
    val chartByMonth: List<Pair<String, Long>> = emptyList(),
    val metrics: GoalMetrics? = null,
    val compare30d: PeriodCompare? = null,
)

data class PeriodCompare(
    val currentMinutes: Long,
    val previousMinutes: Long,
    val deltaRatio: Float, // (current-prev)/prev ; if prev==0 -> 1 when current>0 else 0
)

