package com.example.todo.presentation.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.deepworktracker.domain.model.AlertMode
import com.deepworktracker.domain.model.FocusConfig
import com.deepworktracker.domain.model.Todo
import com.example.todo.R

/**
 * [UI — Screen]
 * Stateless pre-session config sheet shown before the timer starts.
 * Collects FocusConfig locally; emits onStart upward to ViewModel (UDF event ↑).
 * All user-visible text via stringResource (en/vi).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PreFocusConfigBottomSheet(
    todo: Todo,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onStart: (FocusConfig) -> Unit,
) {
    // Type: Int | Sample: 25 from todo.remainingMinutes or estimatedMinutes fallback
    val defaultFocus = (todo.remainingMinutes ?: todo.estimatedMinutes ?: 25).coerceAtLeast(1)

    var focusMinutesText by remember { mutableStateOf(defaultFocus.toString()) }
    var breakMinutesText by remember { mutableStateOf("5") }
    var repeat by remember { mutableStateOf(false) }
    var alertMode by remember { mutableStateOf(AlertMode.NOTIFY) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.focus_config_title),
                style = MaterialTheme.typography.titleLarge,
            )

            val remaining = todo.remainingMinutes ?: todo.estimatedMinutes
            if (remaining != null) {
                Text(
                    text = stringResource(R.string.focus_estimated_remaining, remaining),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            OutlinedTextField(
                value = focusMinutesText,
                onValueChange = { if (it.length <= 3) focusMinutesText = it.filter { c -> c.isDigit() } },
                label = { Text(stringResource(R.string.focus_duration_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = breakMinutesText,
                onValueChange = { if (it.length <= 3) breakMinutesText = it.filter { c -> c.isDigit() } },
                label = { Text(stringResource(R.string.focus_break_duration_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(stringResource(R.string.focus_repeat_pomodoro), style = MaterialTheme.typography.bodyMedium)
                Switch(checked = repeat, onCheckedChange = { repeat = it })
            }

            Column {
                Text(stringResource(R.string.focus_when_timer_ends), style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                AlertMode.entries.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        RadioButton(
                            selected = alertMode == mode,
                            onClick = { alertMode = mode },
                        )
                        Text(
                            text = when (mode) {
                                AlertMode.NOTIFY -> stringResource(R.string.focus_alert_notify)
                                AlertMode.SILENT -> stringResource(R.string.focus_alert_silent)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            val focusMinutes = focusMinutesText.toIntOrNull()?.takeIf { it > 0 }
            val breakMinutes = breakMinutesText.toIntOrNull()?.takeIf { it > 0 } ?: 5

            Button(
                onClick = {
                    if (focusMinutes != null) {
                        // Input: FocusConfig | Output: onStart → ViewModel.startWithConfig()
                        onStart(FocusConfig(focusMinutes, breakMinutes, repeat, alertMode))
                    }
                },
                enabled = focusMinutes != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.focus_start))
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
