package com.example.todo.presentation.countdown

import com.deepworktracker.domain.model.Todo

data class TodoCountdownUiState(
    val todo: Todo? = null,
    val switchableTodos: List<Todo> = emptyList(),
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val isLoading: Boolean = true,
    val error: Throwable? = null,
)

val TodoCountdownUiState.progress: Float
    get() = if (totalSeconds == 0) 0f else remainingSeconds.toFloat() / totalSeconds.toFloat()
