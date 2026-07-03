package com.example.todo.presentation.tododetail.component


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Button
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.deepworktracker.common.datetime.toDdMmYyyyCompact
import com.deepworktracker.common.datetime.toDdMmYyyyCompactOrNull
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.domain.model.TodoStatus
import androidx.compose.ui.res.stringResource
import com.example.todo.R
import com.example.todo.presentation.utils.toChipColor
import com.example.todo.presentation.utils.toChipColorBackground
import com.example.todo.presentation.utils.toDisplayLabel
import com.example.todo.presentation.utils.todoStatusDropdownOptions

@Composable
internal fun TodoDetailContent(
    modifier: Modifier = Modifier,
    todo: Todo,
    isSavingStatus: Boolean,
    isSavingEdit: Boolean,
    isDeleting: Boolean,
    onSetStatus: (TodoStatus) -> Unit,
    onEditClick: () -> Unit,
    onDeadlineEditClick: () -> Unit,
    onStartCountdown: () -> Unit = {},
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

        TodoDateDetail(
            todo = todo,
            enabled = actionsEnabled,
            onEditClick = onEditClick,
            onDeadlineEditClick = onDeadlineEditClick,
        )

        TodoDetailGoal(goal = todo.goal)

        if (todo.status == TodoStatus.TODO || todo.status == TodoStatus.IN_PROGRESS) {
            val remaining = todo.remainingMinutes ?: todo.estimatedMinutes
            val minuteLabel = if (remaining != null) {
                stringResource(R.string.focus_minutes_remaining, remaining)
            } else {
                stringResource(R.string.focus_minutes_default)
            }
            Button(
                onClick = onStartCountdown,
                modifier = Modifier.fillMaxWidth(),
                enabled = actionsEnabled,
            ) {
                Text(stringResource(R.string.focus_start_button, minuteLabel))
            }
        }
    }
}

@Composable
private fun TodoDateDetail(
    todo: Todo,
    enabled: Boolean,
    onEditClick: () -> Unit,
    onDeadlineEditClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val primary = scheme.primary
    val iconBackdrop = primary.copy(alpha = 0.12f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        border = BorderStroke(1.dp, scheme.outlineVariant.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            when (todo.status) {
                TodoStatus.DONE -> {
                    TodoDateDetailRow(
                        modifier = Modifier.fillMaxWidth(),
                        calendarIconBackground = iconBackdrop,
                        label = "Ngày hoàn thành",
                        valueText = todo.completedAt.toDdMmYyyyCompactOrNull() ?: "—",
                        valueHighlighted = true,
                        onEditClick = onEditClick,
                        editEnabled = false,
                    )
                }

                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            TodoDateDetailRow(
                                modifier = Modifier.fillMaxWidth(),
                                calendarIconBackground = iconBackdrop,
                                label = "Ngày tạo",
                                valueText = todo.createdAt.toDdMmYyyyCompact(),
                                valueHighlighted = true,
                                onEditClick = onEditClick,
                                editEnabled = false,
                            )
                        }
                        VerticalDivider(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .heightIn(min = 48.dp),
                            color = scheme.outlineVariant.copy(alpha = 0.45f),
                        )
                        val hasDue = todo.dueAt != null
                        Box(modifier = Modifier.weight(1f)) {
                            TodoDateDetailRow(
                                modifier = Modifier.fillMaxWidth(),
                                calendarIconBackground = iconBackdrop,
                                label = "Ngày hết hạn",
                                valueText = if (hasDue) {
                                    requireNotNull(todo.dueAt).toDdMmYyyyCompactOrNull() ?: "—"
                                } else {
                                    "Chưa có deadline"
                                },
                                valueHighlighted = hasDue,
                                onEditClick = onDeadlineEditClick,
                                editEnabled = enabled,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TodoDateDetailRow(
    calendarIconBackground: Color,
    label: String,
    valueText: String,
    valueHighlighted: Boolean,
    onEditClick: () -> Unit,
    editEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val primary = scheme.primary

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(calendarIconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = primary,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (valueHighlighted) {
                        scheme.onSurface
                    } else {
                        scheme.onSurfaceVariant
                    },
                    fontWeight = if (valueHighlighted) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        OutlinedButton(
            onClick = onEditClick,
            enabled = editEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            border = if (editEnabled) {
                BorderStroke(1.dp, primary)
            } else {
                BorderStroke(1.dp, Color.Transparent)
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = primary,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = scheme.onSurfaceVariant.copy(alpha = 0.5f),
            ),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = "Chỉnh sửa",
                modifier = Modifier.padding(start = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TodoDetailGoal(goal: String) {
    val scheme = MaterialTheme.colorScheme
    Card (
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, scheme.outlineVariant.copy(alpha = 0.55f)),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ){
        Column (
            Modifier.padding(16.dp)
        ){
            Text(
                text = "Mục tiêu",
                style = MaterialTheme.typography.labelLarge,
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                ),
            ) {
                Text(
                    text = goal.ifBlank {
                        "Chưa có mục tiêu"
                    },
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (goal.isBlank()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
            }
        }
    }
}

@Composable
private fun TodoDetailDescriptionCard(description: String) {
    val scheme = MaterialTheme.colorScheme
   Card (
       modifier = Modifier.fillMaxWidth(),
       shape = RoundedCornerShape(12.dp),
       border = BorderStroke(1.dp, scheme.outlineVariant.copy(alpha = 0.55f)),
       colors = CardDefaults.cardColors(containerColor = scheme.surface),
       elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
   ){
       Column (
           Modifier.padding(16.dp)
       ){
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
                    contentDescription = stringResource(R.string.todo_status_picker_content_description),
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                todoStatusDropdownOptions().forEach { (optionStatus, label) ->
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
