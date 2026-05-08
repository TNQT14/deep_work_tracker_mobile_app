package com.example.todo.presentation.tododetail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.deepworktracker.common.datetime.toDdMmYyyyCompact
import com.deepworktracker.common.datetime.toDdMmYyyyCompactOrNull
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.domain.model.TodoStatus
import com.example.todo.presentation.utils.TodoStatusDropdownOptions
import com.example.todo.presentation.utils.toChipColor
import com.example.todo.presentation.utils.toChipColorBackground
import com.example.todo.presentation.utils.toDisplayLabel

@Composable
internal fun TodoDetailContent(
    modifier: Modifier = Modifier,
    todo: Todo,
    isSavingStatus: Boolean,
    isSavingEdit: Boolean,
    isDeleting: Boolean,
    onSetStatus: (TodoStatus) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val scroll = rememberScrollState()
    val actionsEnabled = !isSavingStatus && !isDeleting && !isSavingEdit
    Column(
        modifier = modifier.verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        TodoDetailDescriptionCard(description = todo.description)
        TodoDetailStatusRow(
            status = todo.status,
            enabled = actionsEnabled,
            isSavingStatus = isSavingStatus,
            onSetStatus = onSetStatus,
        )

        TodoDateDetail(todo = todo)

        TodoDetailGoal(goal = todo.goal)



        TodoDetailEditButton(
            enabled = actionsEnabled,
            isSavingEdit = isSavingEdit,
            onClick = onEditClick,
        )

        TodoDetailDeleteButton(
            enabled = actionsEnabled,
            isDeleting = isDeleting,
            onClick = onDeleteClick,
        )
    }
}

@Composable
private fun TodoDateDetail(todo: Todo) {
    if (todo.status == TodoStatus.DONE) {

        Text(
            text = "Ngày hoàn thành: " + todo.completedAt.toDdMmYyyyCompactOrNull(),
        )

    } else {
        Column {
            Text(
                text = "Ngày tạo: " + todo.createdAt.toDdMmYyyyCompact(),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Ngày hết hạn: " + todo.dueAt.toDdMmYyyyCompactOrNull(),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

}

@Composable
private fun TodoDetailGoal(goal: String) {
    Text(
        text = "Goal",
        style = MaterialTheme.typography.labelLarge,
    )
    Text(
        text = goal,
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun TodoDetailDescriptionCard(description: String) {
    Text(
        text = "Mô tả",
        style = MaterialTheme.typography.labelLarge,
    )
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
    ) {
        Text(
            text = description.ifBlank {
                "Chưa có mô tả"
            },
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = if (description.isBlank()) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
    }
}

@Composable
private fun TodoDetailEditButton(
    enabled: Boolean,
    isSavingEdit: Boolean,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isSavingEdit) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(18.dp),
                    strokeWidth = 2.dp,
                )
            }
            Text("Chỉnh sửa việc cần làm")
        }
    }
}

@Composable
private fun TodoDetailDeleteButton(
    enabled: Boolean,
    isDeleting: Boolean,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(18.dp),
                    strokeWidth = 2.dp,
                )
            }
            Text("Xóa todo")
        }
    }
}

@Composable
private fun TodoDetailStatusRow(
    status: TodoStatus,
    enabled: Boolean,
    isSavingStatus: Boolean,
    onSetStatus: (TodoStatus) -> Unit,
) {
    StatusDropdownRow(
        currentLabel = status,
        enabled = enabled,
        isSavingStatus = isSavingStatus,
        onSetStatus = onSetStatus,
    )
}

@Composable
private fun StatusDropdownRow(
    currentLabel: TodoStatus,
    enabled: Boolean,
    isSavingStatus: Boolean,
    onSetStatus: (TodoStatus) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(enabled) {
        if (!enabled) expanded = false
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Trạng thái:",
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(
            modifier = Modifier.weight(1f)
        )
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = enabled,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.outlineVariant
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = currentLabel.toChipColorBackground(),
                    contentColor = currentLabel.toChipColorBackground(),
                    disabledContainerColor = currentLabel.toChipColorBackground(),
                    disabledContentColor = currentLabel.toChipColorBackground(),
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = currentLabel.toDisplayLabel(),
                    color = currentLabel.toChipColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Chọn trạng thái",
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                TodoStatusDropdownOptions.forEach { (optionStatus, label) ->
                    DropdownMenuItem(
                        text = {
                            val statusColor = optionStatus.toChipColor()
                            Text(label, color = statusColor)
                        },
                        onClick = {
                            expanded = false
                            onSetStatus(optionStatus)
                        },
                    )
                }
            }
        }
        if (isSavingStatus) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
            )
        }
    }
}
