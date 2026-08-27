package com.example.todo.presentation.focus

import com.deepworktracker.domain.model.Todo

/**
 * [UiState]
 * Discriminator for Pomodoro phase: active focus work vs break between cycles.
 * Sample: FocusPhase.BREAK after a focus timer hits 0 with repeat=true
 */
enum class FocusPhase { FOCUS, BREAK }

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
     * Type: List<Todo> | Sample: other incomplete todos offered by "Switch task"
     */
    val switchableTodos: List<Todo> = emptyList(),

    /**
     * Type: Boolean | Sample: false after todo is loaded from repository
     */
    val isLoading: Boolean = true,

    /**
     * Type: FocusConfig? | Sample: null until user submits PreFocusConfigBottomSheet;
     *         then FocusConfig(focusMinutes=25, breakMinutes=5, repeat=true, alertMode=NOTIFY)
     */
    val config: com.deepworktracker.domain.model.FocusConfig? = null,

    /**
     * Type: FocusPhase | Sample: FocusPhase.BREAK during Pomodoro rest between cycles
     */
    val phase: FocusPhase = FocusPhase.FOCUS,

    /**
     * Type: Boolean | Sample: true while the 1-second countdown coroutine is active (focus or break)
     */
    val isRunning: Boolean = false,

    /**
     * Type: Boolean | Sample: true after pause() during focus phase; false while running or on break
     */
    val isPaused: Boolean = false,

    /**
     * Type: Int (seconds) | Sample: 1500 when 25-minute focus phase has just started
     */
    val focusRemainingSeconds: Int = 0,

    /**
     * Type: Int (seconds) | Sample: 300 when 5-minute break phase starts after cycle completes
     */
    val breakRemainingSeconds: Int = 0,

    /**
     * Type: Int (seconds) | Sample: 120 after user skips a break with 2 minutes left (in-memory only)
     * Process: incremented by skipBreak(); lost when user leaves the screen
     */
    val accumulatedBreakSeconds: Int = 0,

    /**
     * Type: Int (seconds) | Sample: 600 after 10 minutes of accumulated focus across cycles
     */
    val actualFocusedSeconds: Int = 0,

    /**
     * Type: Int | Sample: 2 after two completed focus phases (incremented at end of each focus timer)
     */
    val cycles: Int = 0,

    /**
     * Type: Int | Sample: 3 after start → pause/resume → continue
     * Process: incremented every time a countdown (re)starts; UI keys progress animation on it
     */
    val timerGeneration: Int = 0,

    /**
     * Type: Throwable? | Sample: null in happy path
     */
    val error: Throwable? = null,
)

/**
 * [UiState]
 * Input: focusRemainingSeconds, config.focusMinutes
 * Process: remaining / total focus phase duration
 * Output: Float in 0f..1f for CircularProgressIndicator (1f = full time left)
 */
val FocusUiState.focusProgress: Float
    get() {
        val total = (config?.focusMinutes ?: 1) * 60
        return if (total == 0) 0f else focusRemainingSeconds.toFloat() / total.toFloat()
    }

/**
 * [UiState]
 * Input: breakRemainingSeconds, config.breakMinutes
 * Process: remaining / total break phase duration
 * Output: Float in 0f..1f for break-phase CircularProgressIndicator
 */
val FocusUiState.breakProgress: Float
    get() {
        val total = (config?.breakMinutes ?: 1) * 60
        return if (total == 0) 0f else breakRemainingSeconds.toFloat() / total.toFloat()
    }

/**
 * [UiState]
 * Input: phase, focusProgress, breakProgress
 * Process: select progress for current Pomodoro phase
 * Output: Float 0f..1f — convenience for phase-agnostic UI (PhaseContent uses phase-specific getters)
 */
val FocusUiState.currentProgress: Float
    get() = if (phase == FocusPhase.FOCUS) focusProgress else breakProgress

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

/**
 * [UiState]
 * Input: accumulatedBreakSeconds
 * Process: integer division by 60
 * Output: Int minutes of skipped break time shown in break-phase UI
 */
val FocusUiState.accumulatedBreakMinutes: Int
    get() = accumulatedBreakSeconds / 60
