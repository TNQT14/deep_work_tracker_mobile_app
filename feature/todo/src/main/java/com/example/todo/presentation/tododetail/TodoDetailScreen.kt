package com.example.todo.presentation.tododetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.domain.model.TodoStatus
import com.deepworktracker.ui.theme.DeepWorkTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun TodoDetailScreen(
    todoId: String,
    onBack: () -> Unit = {},
) {
    val viewModel: TodoDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.deleted.collect {
            onBack()
        }
    }

    LaunchedEffect(uiState.error, uiState.todo) {
        val err = uiState.error ?: return@LaunchedEffect
        if (uiState.todo != null) {
            snackbarHostState.showSnackbar(
                message = err.message ?: "Có lỗi xảy ra",
            )
            viewModel.consumeError()
        }
    }

    DeepWorkTrackerTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(text = "Chi tiết todo") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.todo == null -> {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        if (uiState.error != null) {
                            Text(
                                text = uiState.error!!.message ?: "Có lỗi xảy ra",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        } else {
                            Text(
                                text = "Không tìm thấy todo.",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        TextButton(
                            onClick = onBack,
                            modifier = Modifier.padding(top = 8.dp),
                        ) {
                            Text("Quay lại")
                        }
                    }
                }

                else -> {
                    val todo = uiState.todo!!
                    TodoDetailContent(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                            .fillMaxSize(),
                        todo = todo,
                        isSavingStatus = uiState.isSavingStatus,
                        isDeleting = uiState.isDeleting,
                        onSetStatus = viewModel::setStatus,
                        onDeleteClick = { showDeleteConfirm = true },
                    )
                }
            }

            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text(text = "Xoá todo?") },
                    text = { Text(text = "Todo sẽ bị xóa vĩnh viễn. Hành động này không hoàn tác.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteConfirm = false
                                viewModel.deletedTodo()
                            },
                        ) {
                            Text(
                                text = "Xoá",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text(text = "Huỷ")
                        }
                    },
                )
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

@Composable
private fun TodoDetailContent(
    modifier: Modifier = Modifier,
    todo: Todo,
    isSavingStatus: Boolean,
    isDeleting: Boolean,
    onSetStatus: (TodoStatus) -> Unit,
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
            enabled = !isSavingStatus && !isDeleting,
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
                text = if (todo.description.isBlank()) {
                    "Chưa có mô tả"
                } else {
                    todo.description
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
            onClick = onDeleteClick,
            enabled = !isSavingStatus && !isDeleting,
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

private fun TodoStatus.toDisplayLabel(): String = when (this) {
    TodoStatus.TODO -> "Chưa làm"
    TodoStatus.IN_PROGRESS -> "Đang làm"
    TodoStatus.PAUSED -> "Tạm dừng"
    TodoStatus.DONE -> "Hoàn thành"
}

private val TodoStatusDropdownOptions: List<Pair<TodoStatus, String>> = listOf(
    TodoStatus.TODO to "Chưa làm",
    TodoStatus.IN_PROGRESS to "Đang làm",
    TodoStatus.PAUSED to "Tạm dừng",
    TodoStatus.DONE to "Hoàn thành",
)