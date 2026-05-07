package com.example.todo.presentation.tododetail

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.domain.model.TodoStatus
import com.deepworktracker.ui.theme.DeepWorkTrackerTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    todoId: String,
    onBack: () -> Unit = {}
) {
    val viewModel: TodoDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var showDeleteConfirm by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.deleted.collect {
            onBack()
        }
    }

    DeepWorkTrackerTheme() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Task Detail") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
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
                        uiState.error?.let { e ->
                            Text(
                                text = e.message ?: "Có lỗi xảy ra",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )

                        }
                        Text("Không tìm thấy todo.")
                        TextButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
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
                        onAdvanceStatus = { viewModel.advanceStatus() },
                        onDeleteClick = { showDeleteConfirm = true })
                }
            }
            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text(text = "Xoá Todo?") },
                    text = { Text(text = "Hành động này sẽ không hoàn tác") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteConfirm = false
                                viewModel.deletedTodo()
                            }
                        ) { Text(text = "Xoá") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDeleteConfirm = false
                        }) {
                            Text(text = "Huỷ")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TodoDetailContent(
    modifier: Modifier = Modifier,
    todo: Todo,
    onAdvanceStatus: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = todo.title,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Mô tả",
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = todo.description.ifBlank { "—" },
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = "Trạng thái: ${todo.status.toDisplayLabel()}",
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = "Goal: ${todo.goal}",
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(
            onClick = onAdvanceStatus,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(todo.status.nextButtonLabel())
        }
        OutlinedButton(
            onClick = onDeleteClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Xóa todo")
        }
        val err = (null as Throwable?)
    }
}

private fun TodoStatus.toDisplayLabel(): String = when (this) {
    TodoStatus.TODO -> "Chưa làm"
    TodoStatus.IN_PROGRESS -> "Đang làm"
    TodoStatus.PAUSED -> "Tạm dừng"
    TodoStatus.DONE -> "Hoàn thành"
}

private fun TodoStatus.nextButtonLabel(): String = when (this) {
    TodoStatus.TODO -> "Bắt đầu (→ Đang làm)"
    TodoStatus.IN_PROGRESS -> "Hoàn thành"
    TodoStatus.DONE -> "Làm lại (→ Chưa làm)"
    TodoStatus.PAUSED -> "Tiếp tục (→ Đang làm)"
}
