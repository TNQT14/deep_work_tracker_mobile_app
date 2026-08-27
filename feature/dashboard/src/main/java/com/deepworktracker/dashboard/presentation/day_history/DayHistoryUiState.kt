package com.deepworktracker.dashboard.presentation.day_history

import com.deepworktracker.domain.model.FocusSession
import kotlinx.datetime.LocalDate

typealias SessionGroup = List<FocusSession>
data class DayHistoryUiState (
    val date: LocalDate? = null,
    val groups: List<SessionGroup> = emptyList(),
    val stats: DayStats = DayStats(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class DayStats(
    val sessionCount: Int = 0,
    val totalMs: Long = 0L,
    val interruptedMs: Long = 0L,
    val focusScore: Float = 0f,
)