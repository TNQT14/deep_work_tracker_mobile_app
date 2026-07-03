package com.example.todo.presentation.focus

import com.deepworktracker.domain.model.AlertMode
import com.deepworktracker.domain.model.FocusConfig
import com.deepworktracker.domain.model.Todo

/**
 * [UiState]
 * Immutable screen state for the focus session flow.
 * Single source of truth consumed by FocusScreen via StateFlow.
 */
data class FocusUiState(
    /**
     * Type: Todo? | Sample: Todo(id="abc", title="Write report", estimatedMinutes=25, ...)
     */
    val todo: Todo? = null,

    /**
     * Type: Boolean | Sample: false after todo is loaded from repository
     */
    val isLoading: Boolean = true,

    /**
     * Type: FocusConfig? | Sample: null until user submits PreFocusConfigBottomSheet;
     *         then FocusConfig(focusMinutes=25, breakMinutes=5, repeat=false, alertMode=NOTIFY)
     */
    val config: FocusConfig? = null,

    /**
     * Type: Boolean | Sample: true while the 1-second countdown coroutine is active
     */
    val isRunning: Boolean = false,

    /**
     * Type: Boolean | Sample: true after pause(); false while running or idle
     */
    val isPaused: Boolean = false,

    /**
     * Type: Int (seconds) | Sample: 1500 when 25-minute focus phase has just started
     */
    val focusRemainingSeconds: Int = 0,

    /**
     * Type: Int (seconds) | Sample: 600 after 10 minutes of accumulated focus across cycles
     */
    val actualFocusedSeconds: Int = 0,

    /**
     * Type: Int | Sample: 2 after two completed focus phases (Pomodoro cycles)
     */
    val cycles: Int = 0,

    /**
     * Type: Throwable? | Sample: null in happy path
     */
    val error: Throwable? = null,
)

/**
 * [UiState]
 * Input: focusRemainingSeconds, config.focusMinutes
 * Process: remaining / total phase duration
 * Output: Float in 0f..1f for CircularProgressIndicator (1f = full time left)
 */
val FocusUiState.focusProgress: Float
    get() {
        val total = (config?.focusMinutes ?: 1) * 60
        return if (total == 0) 0f else focusRemainingSeconds.toFloat() / total.toFloat()
    }

/**
 * [UiState]
 * Input: actualFocusedSeconds
 * Process: integer division by 60
 * Output: Int minutes logged for session summary and todo.remainingMinutes deduction
 */
val FocusUiState.actualFocusedMinutes: Int
    get() = actualFocusedSeconds / 60

/**
 * [UiState]
 * Output: true once user has submitted PreFocusConfigBottomSheet (config != null)
 */
val FocusUiState.isConfigured: Boolean
    get() = config != null
