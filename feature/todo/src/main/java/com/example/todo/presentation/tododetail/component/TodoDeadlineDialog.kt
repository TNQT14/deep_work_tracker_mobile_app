package com.example.todo.presentation.tododetail.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.LocalTime as JavaLocalTime
import java.time.ZoneId as JavaZoneId

/**
 * Kết hợp ngày từ DatePicker (millis) với giờ/phút từ TimePicker, theo timezone hệ thống.
 */
private fun instantFromPicker(
    selectedDateMillis: Long,
    hour: Int,
    minute: Int,
): Instant {
    val zone = JavaZoneId.systemDefault()
    val localDate = java.time.Instant.ofEpochMilli(selectedDateMillis)
        .atZone(zone)
        .toLocalDate()
    val javaLdt = JavaLocalDateTime.of(localDate, JavaLocalTime.of(hour, minute))
    val millis = javaLdt.atZone(zone).toInstant().toEpochMilli()
    return Instant.fromEpochMilliseconds(millis)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoDeadlineDialog(
    initialDueAt: Instant?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: suspend (Instant) -> Boolean,
    onClear: suspend () -> Boolean,
    onSaved: () -> Unit,
) {
    val zone = TimeZone.currentSystemDefault()
    val baseline = initialDueAt ?: Clock.System.now()
    val localBaseline = baseline.toLocalDateTime(zone)

    val initialMillis = baseline.toEpochMilliseconds()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    val timePickerState = rememberTimePickerState(
        initialHour = localBaseline.hour,
        initialMinute = localBaseline.minute,
        is24Hour = true,
    )
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    val showClear = initialDueAt != null

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Đặt ngày giờ hết hạn") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(scroll),
            ) {
                DatePicker(state = datePickerState)
                TimePicker(state = timePickerState)
                if (showClear) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                if (onClear()) onSaved()
                            }
                        },
                        enabled = !isSaving,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text("Xóa deadline")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving && datePickerState.selectedDateMillis != null,
                onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@TextButton
                    scope.launch {
                        val instant = instantFromPicker(
                            selectedDateMillis = millis,
                            hour = timePickerState.hour,
                            minute = timePickerState.minute,
                        )
                        if (onSave(instant)) onSaved()
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
