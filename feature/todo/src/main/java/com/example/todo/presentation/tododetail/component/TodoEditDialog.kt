package com.example.todo.presentation.tododetail.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
    onSave: suspend (String, String, String, Int?) -> Boolean,
    onSaved: () -> Unit,
) {
    var goal by remember { mutableStateOf(todo.goal) }
    var title by remember { mutableStateOf(todo.title) }
    var description by remember { mutableStateOf(todo.description) }
    var estimatedMinutesText by remember {
        mutableStateOf(todo.estimatedMinutes?.toString() ?: "")
    }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Chỉnh sửa todo") },
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
                    titleLabel = "Tiêu đề",
                    descriptionLabel = "Mô tả",
                    goalLabel = "Goal",
                    descriptionSingleLine = false,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = estimatedMinutesText,
                    onValueChange = { input ->
                        if (input.length <= 3 && input.all { it.isDigit() }) {
                            estimatedMinutesText = input
                        }
                    },
                    label = { Text("Thời gian tập trung (phút)") },
                    placeholder = { Text("Mặc định: 25 phút") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving && title.trim().isNotEmpty() && goal.trim().isNotEmpty(),
                onClick = {
                    scope.launch {
                        val minutes = estimatedMinutesText.trim().toIntOrNull()?.takeIf { it > 0 }
                        if (onSave(goal, title, description, minutes)) onSaved()
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
