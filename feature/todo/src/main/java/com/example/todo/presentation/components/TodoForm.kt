package com.example.todo.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Cách hiển thị / chỉnh field Goal trên form.
 * - [TodoFormGoalField.Selectable]: dropdown (tạo mới hoặc đổi goal khi sửa).
 * - [TodoFormGoalField.DisplayOnly]: chỉ đọc (thường dùng khi sửa title/description, giữ goal cố định).
 */
sealed interface TodoFormGoalField {
    data class Selectable(val options: List<String>) : TodoFormGoalField
    data object DisplayOnly : TodoFormGoalField
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoForm(
    goal: String,
    title: String,
    description: String,
    goalField: TodoFormGoalField,
    onGoalChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    titleLabel: String = "Title",
    descriptionLabel: String = "Description",
    goalLabel: String = "Goal",
    descriptionSingleLine: Boolean = true,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(titleLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(descriptionLabel) },
            singleLine = descriptionSingleLine,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        when (goalField) {
            is TodoFormGoalField.Selectable -> {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                ) {
                    OutlinedTextField(
                        value = goal,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(goalLabel) },
                        singleLine = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true,
                            ),
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        goalField.options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onGoalChange(option)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }
            TodoFormGoalField.DisplayOnly -> {
                OutlinedTextField(
                    value = goal,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(goalLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
