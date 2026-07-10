package com.deepworktracker.session.presentation.summary

import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.Interruption

data class  SessionSummaryUiState(
    val session: FocusSession? = null,
    val interruptions: List<Interruption> = emptyList(),
    val isLoading: Boolean = true,
    val error: Throwable? = null,
)
