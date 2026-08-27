package com.example.todo.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.ui.theme.tokens.Spacing
import com.example.todo.R

@Composable
internal fun SwitchTaskPickerDialog(
    todos: List<Todo>,
    onTodoSelected: (Todo) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.focus_switch_task_title)) },
        text = {
            if (todos.isEmpty()) {
                Text(stringResource(R.string.focus_switch_task_empty))
            } else {
                Column(
                    modifier = Modifier
                        .heightIn(max = Spacing.xxl * 6)
                        .verticalScroll(rememberScrollState()),
                ) {
                    todos.forEach { todo ->
                        Text(
                            text = todo.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTodoSelected(todo) }
                                .padding(vertical = Spacing.sm),
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.focus_switch_task_cancel))
            }
        },
    )
}
