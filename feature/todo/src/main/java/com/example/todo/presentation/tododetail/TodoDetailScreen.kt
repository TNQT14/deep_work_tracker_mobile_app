package com.example.todo.presentation.tododetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.semantics.Role
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

        AssistChip(
            onClick = {},
            enabled = false,
            label = { Text(todo.status.toDisplayLabel()) },
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

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Trạng thái",
                style = MaterialTheme.typography.labelLarge,
            )
            if (isSavingStatus) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                )
            }
        }
        Column(modifier = Modifier.selectableGroup()) {
            TodoStatusRadioOptions.forEach { (status, label) ->
                val enabled = !isSavingStatus && !isDeleting
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .selectable(
                            selected = todo.status == status,
                            enabled = enabled,
                            onClick = { onSetStatus(status) },
                            role = Role.RadioButton,
                        )
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = todo.status == status,
                        onClick = null,
                        enabled = enabled,
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
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

private val TodoStatusRadioOptions: List<Pair<TodoStatus, String>> = listOf(
    TodoStatus.TODO to "Chưa làm",
    TodoStatus.IN_PROGRESS to "Đang làm",
    TodoStatus.PAUSED to "Tạm dừng",
    TodoStatus.DONE to "Hoàn thành",
)