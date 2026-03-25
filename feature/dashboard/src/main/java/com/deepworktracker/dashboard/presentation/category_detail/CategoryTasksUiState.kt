package com.deepworktracker.dashboard.presentation.category_detail

import com.deepworktracker.domain.model.Todo

data class CategoryTasksUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val todos: List<Todo> = emptyList(),
)
