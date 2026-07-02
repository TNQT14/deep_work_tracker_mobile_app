package com.example.todo.presentation.todolist

import android.se.omapi.Session
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.domain.model.TodoStatus

data class TodoListUiState (
    val isLoading: Boolean = false,
    val error: Throwable ?= null,
    val todos: List<Todo> = emptyList(),
    val session: List<FocusSession> = emptyList(),
    val sortType: TodoSortType = TodoSortType.CREATED_AT,
    val selectedStatus: TodoStatus? = null,
)