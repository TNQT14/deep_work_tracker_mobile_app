package com.deepworktracker.dashboard.presentation.day_history

import com.deepworktracker.domain.model.FocusSession
import kotlinx.datetime.LocalDate

data class DayHistoryUiState (
    val date: LocalDate? = null,
    val sessions: List<FocusSession> = emptyList(),
    val activeSession: FocusSession? =null,
    val isLoading: Boolean = true,
    val error: String? = null
)