package com.example.todo.presentation.utils

import com.deepworktracker.domain.model.TodoStatus

internal fun TodoStatus.toDisplayLabel(): String = when (this) {
    TodoStatus.TODO -> "Chưa làm"
    TodoStatus.IN_PROGRESS -> "Đang làm"
    TodoStatus.PAUSED -> "Tạm dừng"
    TodoStatus.DONE -> "Hoàn thành"
}

internal val TodoStatusDropdownOptions: List<Pair<TodoStatus, String>> = listOf(
    TodoStatus.TODO to "Chưa làm",
    TodoStatus.IN_PROGRESS to "Đang làm",
    TodoStatus.PAUSED to "Tạm dừng",
    TodoStatus.DONE to "Hoàn thành",
)
