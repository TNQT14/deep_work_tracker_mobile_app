package com.example.todo.presentation.tododetail.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.deepworktracker.domain.model.Todo
import com.example.todo.presentation.components.TodoForm
import com.example.todo.presentation.components.TodoFormGoalField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoEditDialog(
    todo: Todo,
    goalOptions: List<String>,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: suspend (String, String, String) -> Boolean,
    onSaved: () -> Unit,
) {
    var goal by remember { mutableStateOf(todo.goal) }
    var title by remember { mutableStateOf(todo.title) }
    var description by remember { mutableStateOf(todo.description) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Chỉnh sửa todo") },
        text = {
            TodoForm(
                goal = goal,
                title = title,
                description = description,
                goalField = TodoFormGoalField.Selectable(goalOptions),
                onGoalChange = { goal = it },
                onTitleChange = { title = it },
                onDescriptionChange = { description = it },
                titleLabel = "Tiêu đề",
                descriptionLabel = "Mô tả",
                goalLabel = "Goal",
                descriptionSingleLine = false,
            )
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving && title.trim().isNotEmpty() && goal.trim().isNotEmpty(),
                onClick = {
                    scope.launch {
                        if (onSave(goal, title, description)) onSaved()
                    }
                },
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving,
            ) {
                Text("Huỷ")
            }
        },
    )
}
