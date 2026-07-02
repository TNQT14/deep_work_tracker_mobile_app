package com.deepworktracker.todo.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.domain.model.TodoStatus
import com.example.todo.presentation.components.TodoForm
import com.example.todo.presentation.components.TodoFormGoalField
import com.example.todo.presentation.todolist.TodoListViewModel
import com.example.todo.presentation.todolist.TodoSortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onNavigateToTodoDetail: (String) -> Unit = {}
) {
    val viewModel: TodoListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var goal by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    Scaffold(topBar = {
            TopAppBar(
                title = { Text(text = "Todo List") })
        }, floatingActionButton = {
            FloatingActionButton(onClick = { showForm = !showForm }) {
                Icon(Icons.Default.Add, contentDescription = "Add todo")
            }
        }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                if (showForm) {
                    val goalOptions = uiState.session.map { it.goal }.distinct().sorted()

                    AlertDialog(
                        onDismissRequest = { showForm = false },
                        title = {
                            Text("Add Todo")
                        },
                        text = {
                            Column {
                                TodoForm(
                                    goal = goal,
                                    title = title,
                                    description = description,
                                    goalField = TodoFormGoalField.Selectable(goalOptions),
                                    onGoalChange = { goal = it },
                                    onTitleChange = { title = it },
                                    onDescriptionChange = { description = it },
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                Log.d("TodoListScreen", "TodoListScreen: $goal $title $description")
                                viewModel.addTodo(
                                    goal = goal,
                                    title = title,
                                    description = description
                                )
                                title = ""
                                description = ""
                                showForm = false

                            }) { Text("Add") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showForm = false }) { Text("Cancel") }
                        }
                    )

                }
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    uiState.error != null -> {
                        Text(
                            text = uiState.error?.localizedMessage ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    else -> {
                        Spacer(Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = "Danh sách (${uiState.todos.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { viewModel.onSortTypeChange(TodoSortType.NAME) }) {
                                Icon(
                                    imageVector = Icons.Filled.SortByAlpha,
                                    contentDescription = "Sort alphabetically",
                                    tint = Color.Black
                                )
                            }
                            IconButton(onClick = { viewModel.onSortTypeChange(TodoSortType.CREATED_AT) }) {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = "Sort alphabetically",
                                    tint = Color.Black
                                )
                            }
                            IconButton(onClick = { viewModel.onSortTypeChange(TodoSortType.GOAL) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Sort,
                                    contentDescription = "Sort alphabetically",
                                    tint = Color.Black
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FilterChip(
                                selected = uiState.selectedStatus == null,
                                onClick = { viewModel.onStatusFilterChange(null) },
                                label = { Text("Tất cả") },
                            )
                            FilterChip(
                                selected = uiState.selectedStatus == TodoStatus.TODO,
                                onClick = { viewModel.onStatusFilterChange(TodoStatus.TODO) },
                                label = { Text("Việc cần làm") },
                            )
                            FilterChip(
                                selected = uiState.selectedStatus == TodoStatus.IN_PROGRESS,
                                onClick = { viewModel.onStatusFilterChange(TodoStatus.IN_PROGRESS) },
                                label = { Text("Đang làm") },
                            )
                            FilterChip(
                                selected = uiState.selectedStatus == TodoStatus.PAUSED,
                                onClick = { viewModel.onStatusFilterChange(TodoStatus.PAUSED) },
                                label = { Text("Tạm dừng") },
                            )
                            FilterChip(
                                selected = uiState.selectedStatus == TodoStatus.DONE,
                                onClick = { viewModel.onStatusFilterChange(TodoStatus.DONE) },
                                label = { Text("Hoàn thành") },
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        if (uiState.todos.isEmpty()) {
                            Text(
                                text = if (uiState.selectedStatus != null) "Không có todo nào." else "Chưa có todo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(uiState.todos, key = { it.id }) { todo ->
                                    val dismissBoxState = rememberSwipeToDismissBoxState()
                                    val shape = MaterialTheme.shapes.medium
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(shape),
                                    )
                                    {
                                        SwipeToDismissBox(
                                            state = dismissBoxState,
                                            enableDismissFromStartToEnd = false,
                                            enableDismissFromEndToStart = true,
                                            onDismiss = { value ->
                                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                                    viewModel.deleteTodo(todo)
                                                }
                                            },
                                            backgroundContent = {
                                                val color = when (dismissBoxState.targetValue) {
                                                    SwipeToDismissBoxValue.EndToStart ->
                                                        Color.Yellow
                                                    else -> Color.Red
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(color).padding(14.dp),
                                                    contentAlignment = Alignment.CenterEnd,
                                                )
                                                {
                                                    Icon(
                                                        imageVector = Icons.Filled.Delete,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                                    )
                                                }
                                            },
                                            content = {
                                                TodoRow(
                                                    todo = todo,
                                                    done = todo.status == TodoStatus.DONE,
                                                    onTap = { onNavigateToTodoDetail(todo.id) },
                                                    onToggle = { viewModel.updateTodo(todo) },
                                                    onDelete = { viewModel.deleteTodo(todo) },
                                                    shape = shape,
                                                )
                                            },
                                        )
                                    }
                                }

                            }
                    }
                }
            }
        }
    }
}

@Composable
private fun TodoRow(
    todo: Todo,
    done: Boolean,
    onTap: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        shape = shape,) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = done,
                onCheckedChange = { onToggle() }
            )
            Column(
                modifier = Modifier.weight(1f)

            ) {
                Row {
                    Text(
                        text = todo.title
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = todo.goal,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.padding(0.dp))
                Text(
                    text = todo.description,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
                Spacer(Modifier.padding(0.dp))

                Text(
                    text = "Ngày tạo: " + todo.createdAt.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
                Spacer(Modifier.padding(0.dp))

                if (todo.dueAt != null) {
                    Text(
                        text = "Ngày tới hạn: " + todo.dueAt.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }


            }
            if (done) {
                Icon(
                    imageVector = Icons.Filled.TaskAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null
                )
            }
        }
    }
}