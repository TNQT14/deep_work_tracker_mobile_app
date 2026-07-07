package com.deepworktracker.profile.presentation.profile_screen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.deepworktracker.profile.R

/**
 * [UI — Screen]
 * Stateless edit-profile dialog: full name (required) + optional new password.
 * Blank password means "keep current" — matches backend's empty-string-is-no-op contract.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditProfileDialog(
    currentName: String,
    isSaving: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSave: (fullName: String, password: String) -> Unit,
) {
    var fullName by remember { mutableStateOf(currentName) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var mismatchError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text(stringResource(R.string.profile_edit_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(stringResource(R.string.profile_full_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        mismatchError = false
                    },
                    label = { Text(stringResource(R.string.profile_new_password_label)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        mismatchError = false
                    },
                    label = { Text(stringResource(R.string.profile_confirm_password_label)) },
                    singleLine = true,
                    isError = mismatchError,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (mismatchError) {
                    Text(
                        text = stringResource(R.string.profile_password_mismatch),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving && fullName.isNotBlank(),
                onClick = {
                    if (newPassword != confirmPassword) {
                        mismatchError = true
                        return@TextButton
                    }
                    onSave(fullName.trim(), newPassword)
                },
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.height(16.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.profile_edit_save))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text(stringResource(R.string.profile_edit_cancel))
            }
        },
    )
}
