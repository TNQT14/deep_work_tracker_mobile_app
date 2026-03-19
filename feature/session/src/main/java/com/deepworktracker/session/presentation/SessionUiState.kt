package com.deepworktracker.session.presentation

import com.deepworktracker.domain.model.FocusSession
import kotlin.time.Duration

data class SessionUiState(
    val session: FocusSession? = null,
    val isTracking: Boolean = false,
    val elapsedTime: Duration = Duration.ZERO,
    val error: Throwable? = null,
    val isLoading: Boolean = false,
    val recentSession: List<String> = emptyList(),
    val recentCategories: List<String> = emptyList(),
    val recentTags: List<String> = emptyList(),
    val suggestedCategory: String? = null,
    val suggestedTag: String? = null,
)
