package com.deepworktracker.profile.presentation.profile_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepworktracker.common.time.TimeFormatter
import com.deepworktracker.profile.R
import com.deepworktracker.profile.presentation.EditProfileState
import com.deepworktracker.profile.presentation.LogoutState
import com.deepworktracker.profile.presentation.ProfileUiState
import com.deepworktracker.profile.presentation.ProfileViewModel
import com.deepworktracker.profile.presentation.profile_screen.component.EditProfileDialog
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ProfileRoute(
    onLogoutSuccess: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEditDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(uiState.logoutState) {
        if (uiState.logoutState is LogoutState.Success) {
            onLogoutSuccess()
            viewModel.consumeLogoutSuccess()
        }
    }

    LaunchedEffect(uiState.editProfileState) {
        if (uiState.editProfileState is EditProfileState.Success) {
            showEditDialog = false
            viewModel.consumeEditProfileSuccess()
        }
    }

    ProfileScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onClearError = viewModel::clearError,
        onLogout = viewModel::logout,
        onOpenSettings = onNavigateToSettings,
        onEditProfileClick = { showEditDialog = true },
    )

    if (showEditDialog) {
        EditProfileDialog(
            currentName = uiState.userName,
            isSaving = uiState.editProfileState is EditProfileState.Loading,
            errorMessage = (uiState.editProfileState as? EditProfileState.Error)?.message,
            onDismiss = {
                showEditDialog = false
                viewModel.clearEditProfileError()
            },
            onSave = { fullName, password -> viewModel.updateProfile(fullName, password) },
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onRefresh: () -> Unit,
    onClearError: () -> Unit,
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit,
    onEditProfileClick: () -> Unit = {},
) {
    val isLoggingOut = uiState.logoutState is LogoutState.Loading

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                actions = {
                    IconButton(
                        onClick = onRefresh,
                        enabled = !uiState.isLoading && !isLoggingOut
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(
                                    R.string.profile_refresh_content_description,
                                ),
                            )
                        }
                    }
                }
            )

            if (uiState.isLoading && uiState.totalSessions == 0) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = stringResource(
                                    R.string.profile_avatar_content_description,
                                ),
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = uiState.userName.ifEmpty {
                                    stringResource(R.string.profile_default_user)
                                },
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            if (uiState.email.isNotEmpty()) {
                                Text(
                                    text = uiState.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            TextButton(onClick = onEditProfileClick) {
                                Text(stringResource(R.string.profile_edit_button))
                            }

                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.profile_statistics),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            StatRow(
                                label = stringResource(R.string.profile_total_sessions),
                                value = "${uiState.totalSessions}",
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            StatRow(
                                label = stringResource(R.string.profile_total_focus_time),
                                value = TimeFormatter.formatDurationShort(
                                    uiState.totalFocusTime.milliseconds
                                )
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.profile_settings),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            TextButton(onClick = onOpenSettings) {
                                Text(stringResource(R.string.profile_open_settings))
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onLogout,
                enabled = !isLoggingOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                if (isLoggingOut) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(stringResource(R.string.profile_logout))
                }
            }

            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error.message ?: stringResource(R.string.profile_error_generic),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onClearError) {
                            Text(stringResource(R.string.profile_dismiss))
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun StatRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
