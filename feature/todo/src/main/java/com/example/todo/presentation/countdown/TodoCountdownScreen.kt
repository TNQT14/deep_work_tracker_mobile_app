package com.example.todo.presentation.countdown

import android.media.RingtoneManager
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoCountdownScreen(
    onBack: () -> Unit = {},
    viewModel: TodoCountdownViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFinishDialog by remember { mutableStateOf(false) }
    var showBackConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler(enabled = uiState.isRunning || uiState.isPaused) {
        showBackConfirmDialog = true
    }

    val window = (context as? android.app.Activity)?.window
    DisposableEffect(uiState.isRunning) {
        if (uiState.isRunning) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) {
            showFinishDialog = true
            try {
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(context, uri)
                ringtone?.play()
            } catch (_: Exception) {
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.finishedEvent.collect { onBack() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(uiState.todo?.title ?: stringResource(R.string.countdown_default_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.countdown_back_content_description),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    uiState.todo?.goal?.takeIf { it.isNotBlank() }?.let { goal ->
                        Text(
                            text = goal,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val progressTarget = if (uiState.isRunning && uiState.remainingSeconds > 0) {
                        (uiState.remainingSeconds - 1).toFloat() / uiState.totalSeconds
                    } else {
                        uiState.progress
                    }
                    val animatedProgress by animateFloatAsState(
                        targetValue = progressTarget,
                        animationSpec = if (uiState.isRunning) {
                            tween(durationMillis = 1000, easing = LinearEasing)
                        } else {
                            snap()
                        },
                        label = "countdownProgress",
                    )

                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(220.dp),
                            strokeWidth = 10.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = formatCountdown(uiState.remainingSeconds),
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = stringResource(R.string.countdown_remaining_label),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    when {
                        !uiState.isRunning && !uiState.isPaused -> {
                            Button(
                                onClick = { viewModel.start() },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(stringResource(R.string.countdown_start))
                            }
                        }

                        uiState.isRunning -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.pause() },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(stringResource(R.string.countdown_pause))
                                }
                                OutlinedButton(
                                    onClick = { viewModel.finish(markDone = true) },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(stringResource(R.string.countdown_end_early))
                                }
                            }
                        }

                        uiState.isPaused -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Button(
                                    onClick = { viewModel.resume() },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(stringResource(R.string.countdown_resume))
                                }
                                OutlinedButton(
                                    onClick = { viewModel.finish(markDone = false) },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(stringResource(R.string.countdown_end))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBackConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBackConfirmDialog = false },
            title = { Text(stringResource(R.string.countdown_back_confirm_title)) },
            text = { Text(stringResource(R.string.countdown_back_confirm_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showBackConfirmDialog = false
                        viewModel.finish(markDone = false)
                    }
                ) {
                    Text(stringResource(R.string.countdown_back_confirm_exit))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackConfirmDialog = false }) {
                    Text(stringResource(R.string.countdown_back_confirm_continue))
                }
            },
        )
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.countdown_finish_dialog_title)) },
            text = { Text(stringResource(R.string.countdown_finish_dialog_message)) },
            confirmButton = {
                Button(onClick = {
                    showFinishDialog = false
                    viewModel.finish(markDone = true)
                }) {
                    Text(stringResource(R.string.countdown_mark_done))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showFinishDialog = false
                    viewModel.finish(markDone = false)
                }) {
                    Text(stringResource(R.string.countdown_dismiss))
                }
            },
        )
    }
}

private fun formatCountdown(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
