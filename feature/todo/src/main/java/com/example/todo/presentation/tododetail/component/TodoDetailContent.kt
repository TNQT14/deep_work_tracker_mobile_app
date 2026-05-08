package com.example.todo.presentation.tododetail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.domain.model.TodoStatus
import com.example.todo.presentation.utils.TodoStatusDropdownOptions
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
    Column(
        modifier = modifier.verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = todo.title,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )

        StatusDropdownRow(
            currentLabel = todo.status.toDisplayLabel(),
            enabled = !isSavingStatus && !isDeleting && !isSavingEdit,
            isSavingStatus = isSavingStatus,
            onSetStatus = onSetStatus,
        )

        Text(
            text = "Goal",
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = todo.goal,
            style = MaterialTheme.typography.bodyLarge,
        )

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
                text = todo.description.ifBlank {
                    "Chưa có mô tả"
                },
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = if (todo.description.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }

        OutlinedButton(
            onClick = onEditClick,
            enabled = !isSavingStatus && !isDeleting && !isSavingEdit,
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

        OutlinedButton(
            onClick = onDeleteClick,
            enabled = !isSavingStatus && !isDeleting && !isSavingEdit,
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
}

@Composable
private fun StatusDropdownRow(
    currentLabel: String,
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
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = enabled,
            ) {
                Text(
                    text = currentLabel,
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
                TodoStatusDropdownOptions.forEach { (status, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            expanded = false
                            onSetStatus(status)
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
