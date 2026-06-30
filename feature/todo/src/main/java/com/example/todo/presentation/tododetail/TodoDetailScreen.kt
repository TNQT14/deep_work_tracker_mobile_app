package com.example.todo.presentation.tododetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todo.presentation.tododetail.component.TodoDeadlineDialog
import com.example.todo.presentation.tododetail.component.TodoDetailContent
import com.example.todo.presentation.tododetail.component.TodoEditDialog

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun TodoDetailScreen(
    todoId: String,
    onBack: () -> Unit = {},
    onNavigateToCountdown: (todoId: String) -> Unit = {},
) {
    val viewModel: TodoDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeadlineDialog by remember { mutableStateOf(false) }
    var overflowMenuExpanded by remember { mutableStateOf(false) }

    val openEditDialog = remember { { showEditDialog = true } }
    val openDeleteConfirm = remember { { showDeleteConfirm = true } }
    val openDeadlineDialog = remember { { showDeadlineDialog = true } }

    LaunchedEffect(Unit) {
        viewModel.deleted.collect {
            onBack()
        }
    }

    LaunchedEffect(uiState.todo) {
        if (uiState.todo == null) {
            overflowMenuExpanded = false
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when {
                                uiState.todo != null -> uiState.todo!!.title
                                uiState.isLoading -> "Đang tải"
                                else -> "Todo"
                            },
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                            )
                        }
                    },
                    actions = {
                        if (uiState.todo != null) {
                            Box {
                                IconButton(onClick = { overflowMenuExpanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Tuỳ chọn",
                                    )
                                }
                                DropdownMenu(
                                    expanded = overflowMenuExpanded,
                                    onDismissRequest = { overflowMenuExpanded = false },
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Sửa") },
                                        onClick = {
                                            overflowMenuExpanded = false
                                            openEditDialog()
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                            )
                                        },
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Xoá") },
                                        onClick = {
                                            overflowMenuExpanded = false
                                            openDeleteConfirm()
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                            )
                                        },
                                    )
                                }
                            }
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
                    val goalOptionsForForm = remember(uiState.goalOptions, todo.goal) {
                        (uiState.goalOptions + todo.goal).distinct().sorted()
                    }
                    TodoDetailContent(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                            .fillMaxSize(),
                        todo = todo,
                        isSavingStatus = uiState.isSavingStatus,
                        isSavingEdit = uiState.isSavingEdit,
                        isDeleting = uiState.isDeleting,
                        onSetStatus = viewModel::setStatus,
                        onEditClick = openEditDialog,
                        onDeadlineEditClick = openDeadlineDialog,
                        onStartCountdown = { onNavigateToCountdown(todoId) },
                    )

                    if (showDeadlineDialog) {
                        TodoDeadlineDialog(
                            initialDueAt = todo.dueAt,
                            isSaving = uiState.isSavingEdit,
                            onDismiss = { if (!uiState.isSavingEdit) showDeadlineDialog = false },
                            onSave = viewModel::updateDeadline,
                            onClear = viewModel::clearDeadline,
                            onSaved = { showDeadlineDialog = false },
                        )
                    }

                    if (showEditDialog) {
                        TodoEditDialog(
                            todo = todo,
                            goalOptions = goalOptionsForForm,
                            isSaving = uiState.isSavingEdit,
                            onDismiss = { if (!uiState.isSavingEdit) showEditDialog = false },
                            onSave = viewModel::updateTodo,
                            onSaved = { showEditDialog = false },
                        )
                    }
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