package com.example.todo.presentation.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.deepworktracker.domain.model.TodoStatus
import com.deepworktracker.ui.theme.tokens.ComponentColors
import com.example.todo.R

@Composable
internal fun TodoStatus.toDisplayLabel(): String = when (this) {
    TodoStatus.TODO -> stringResource(R.string.todo_status_todo)
    TodoStatus.IN_PROGRESS -> stringResource(R.string.todo_status_in_progress)
    TodoStatus.PAUSED -> stringResource(R.string.todo_status_paused)
    TodoStatus.DONE -> stringResource(R.string.todo_status_done)
}

@Composable
internal fun todoStatusDropdownOptions(): List<Pair<TodoStatus, String>> =
    TodoStatus.entries.map { status -> status to status.toDisplayLabel() }

@Composable
internal fun TodoStatus.toChipColor(): Color = when (this) {
    TodoStatus.TODO -> MaterialTheme.colorScheme.outline
    TodoStatus.IN_PROGRESS -> ComponentColors.todoInProgress
    TodoStatus.PAUSED -> ComponentColors.todoPaused
    TodoStatus.DONE -> ComponentColors.todoDone
}

@Composable
internal fun TodoStatus.toChipColorBackground(): Color = when (this) {
    TodoStatus.TODO -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    TodoStatus.IN_PROGRESS -> ComponentColors.todoInProgressBackground()
    TodoStatus.PAUSED -> ComponentColors.todoPausedBackground()
    TodoStatus.DONE -> ComponentColors.todoDoneBackground()
}
