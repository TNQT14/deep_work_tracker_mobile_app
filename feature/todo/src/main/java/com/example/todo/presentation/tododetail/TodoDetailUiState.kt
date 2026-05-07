package com.example.todo.presentation.tododetail

import com.deepworktracker.domain.model.Todo

data class TodoDetailUiState(
    val isLoading: Boolean = false,
    val isSavingStatus: Boolean = false,
    val isDeleting: Boolean = false,
    val error: Throwable? = null,
    val todo: Todo? = null,
)
