package com.example.todo.presentation.focushistory

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * [UiState]
 * One completed focus session as shown in the history list.
 * Sample: FocusSessionItem(id="s1", focusedMinutes=48, cycles=2, repeat=true, breakMinutes=5)
 */
data class FocusSessionItem(
    val id: String,
    val startTime: Instant,
    val endTime: Instant,
    val focusedMinutes: Int,
    val cycles: Int,
    val repeat: Boolean,
    val breakMinutes: Int,
)

/**
 * [UiState]
 * Sessions grouped under one calendar day (section header in the list).
 */
data class FocusDaySection(
    val date: LocalDate,
    val items: List<FocusSessionItem>,
)

/**
 * [UiState]
 * Immutable screen state for the per-todo focus history.
 */
data class FocusHistoryUiState(
    val isLoading: Boolean = true,
    val todoTitle: String = "",
    val sections: List<FocusDaySection> = emptyList(),
    val totalFocusedMinutes: Int = 0,
    val sessionCount: Int = 0,
    val totalCycles: Int = 0,
)
