package com.example.todo.presentation.todolist

import android.se.omapi.Session
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.domain.model.TodoStatus

/**
 * [UiState]
 * Immutable screen state for TodoListScreen.
 * Type: data class | Sample: TodoListUiState(isLoading=false, todos=[Todo(...)], sortType=CREATED_AT)
 */
data class TodoListUiState (
    /** Type: Boolean | Sample: false — true while initial load or mutation in flight */
    val isLoading: Boolean = false,
    /** Type: Throwable? | Sample: null — set when create/update/delete fails */
    val error: Throwable ?= null,
    /** Type: List<Todo> | Sample: sorted/filtered view shown in LazyColumn (not raw DB list) */
    val todos: List<Todo> = emptyList(),
    /** Type: List<FocusSession> | Sample: [FocusSession(goal="Deep work", ...)] — goal picker options */
    val session: List<FocusSession> = emptyList(),
    /** Type: TodoSortType | Sample: TodoSortType.CREATED_AT */
    val sortType: TodoSortType = TodoSortType.CREATED_AT,
    /** Type: TodoStatus? | Sample: null (all) | TodoStatus.TODO — client-side filter */
    val selectedStatus: TodoStatus? = null,
)
