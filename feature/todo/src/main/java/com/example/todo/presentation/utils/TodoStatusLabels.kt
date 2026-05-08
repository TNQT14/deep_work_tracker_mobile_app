package com.example.todo.presentation.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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

@Composable
internal fun TodoStatus.toChipColor(): Color =  when(this){
    TodoStatus.TODO -> MaterialTheme.colorScheme.outline
    TodoStatus.IN_PROGRESS -> Color(0xFF1E88E5)
    TodoStatus.PAUSED -> Color(0xFFF9A825)
    TodoStatus.DONE -> Color(0xFF43A047)
}
@Composable
internal fun TodoStatus.toChipColorBackground(): Color =  when (this) {
    TodoStatus.TODO -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    TodoStatus.IN_PROGRESS -> Color(0xFF1E88E5).copy(alpha = 0.2f)
    TodoStatus.PAUSED -> Color(0xFFF9A825).copy(alpha = 0.2f)
    TodoStatus.DONE -> Color(0xFF43A047).copy(alpha = 0.2f)
}

