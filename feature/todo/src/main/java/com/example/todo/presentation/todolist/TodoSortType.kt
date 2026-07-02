package com.example.todo.presentation.todolist

/**
 * [UiState]
 * Sort options for the todo list. Applied client-side in TodoListViewModel.applySort().
 */
enum class TodoSortType {
    /** Sort by todo.title ascending */
    NAME,
    /** Sort by todo.createdAt descending (newest first) */
    CREATED_AT,
    /** Sort by todo.goal ascending */
    GOAL
}
