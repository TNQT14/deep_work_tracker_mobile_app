package com.example.todo.presentation.focus

import android.media.RingtoneManager
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.domain.model.AlertMode
import com.example.todo.R

/**
 * [UI — Route]
 * Wires FocusViewModel, collects StateFlow, handles Channel effects (sound, dialogs, navigate back).
 * Branches: loading → config sheet → active timer UI.
 * Note: uses collectAsState() (legacy in this module); prefer collectAsStateWithLifecycle in new code.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    onBack: () -> Unit,
    viewModel: FocusViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val window = (context as? android.app.Activity)?.window

    var showEndDialog by remember { mutableStateOf(false) }
    var showBackConfirmDialog by remember { mutableStateOf(false) }
    val configSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // [Effect] Keep screen awake while timer is running
    DisposableEffect(uiState.isRunning) {
        if (uiState.isRunning) window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }

    // [Effect] Pomodoro cycle complete with repeat=ON — play notification sound only
    LaunchedEffect(Unit) {
        viewModel.cycleCompletedEvent.collect {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            RingtoneManager.getRingtone(context, uri)?.play()
        }
    }

    // [Effect] Timer finished with repeat=OFF — optional sound then show EndSessionDialog
    LaunchedEffect(Unit) {
        viewModel.timerFinishedEvent.collect {
            if (uiState.config?.alertMode == AlertMode.NOTIFY) {
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                RingtoneManager.getRingtone(context, uri)?.play()
            }
            showEndDialog = true
        }
    }

    // [Effect] Navigate back after endSession() persists and sends doneEvent
    LaunchedEffect(Unit) {
        viewModel.doneEvent.collect { onBack() }
    }

    // [Effect] Intercept system back while session is active
    BackHandler(enabled = uiState.isRunning || uiState.isPaused) {
        showBackConfirmDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.todo?.title ?: "",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
            )
        },
    ) { padding ->

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            !uiState.isConfigured -> {
                uiState.todo?.let { todo ->
                    PreFocusConfigBottomSheet(
                        todo = todo,
                        sheetState = configSheetState,
                        onDismiss = onBack,
                        onStart = { config -> viewModel.startWithConfig(config) },
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }

            else -> {
                PhaseContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    phase = uiState.phase,
                    uiState = uiState,
                    onPause = { viewModel.pause() },
                    onResume = { viewModel.resume() },
                    onSkipBreak = { viewModel.skipBreak() },
                    onEndClick = { showEndDialog = true },
                )
            }
        }
    }

    if (showEndDialog) {
        EndSessionDialog(
            uiState = uiState,
            onContinue = {
                showEndDialog = false
                viewModel.continueSession()
            },
            onEnd = { markDone ->
                showEndDialog = false
                viewModel.endSession(markDone)
            },
        )
    }

    if (showBackConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBackConfirmDialog = false },
            title = { Text(stringResource(R.string.countdown_back_confirm_title)) },
            text = { Text(stringResource(R.string.focus_back_confirm_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showBackConfirmDialog = false
                        showEndDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                ) { Text(stringResource(R.string.focus_back_confirm_end)) }
            },
            dismissButton = {
                TextButton(onClick = { showBackConfirmDialog = false }) {
                    Text(stringResource(R.string.countdown_back_confirm_continue))
                }
            },
        )
    }
}

/**
 * [UI — Screen]
 * Stateless timer UI for focus or break phase: circular progress, primary action, end session.
 * Receives phase + uiState + callbacks only (no ViewModel).
 * FOCUS: pause/resume FAB; BREAK: skip-break button + accumulated-break label.
 * phase: FocusPhase — e.g. FocusPhase.BREAK during Pomodoro rest
 * onSkipBreak: [UDF] event ↑ → ViewModel.skipBreak() rolls unused time into accumulatedBreakSeconds
 */
@Composable
private fun PhaseContent(
    modifier: Modifier = Modifier,
    phase: FocusPhase,
    uiState: FocusUiState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onSkipBreak: () -> Unit,
    onEndClick: () -> Unit,
) {
    // Type: Color | Sample: 0xFF4CAF50 green accent for break phase
    val breakColor = androidx.compose.ui.graphics.Color(0xFF4CAF50)
    // Type: Color | primary for FOCUS, breakColor for BREAK
    val accentColor = when (phase) {
        FocusPhase.FOCUS -> MaterialTheme.colorScheme.primary
        FocusPhase.BREAK -> breakColor
    }
    // Type: Int (seconds) | Sample: 180 during a 5-minute break with 3 min left
    val remainingSeconds = when (phase) {
        FocusPhase.FOCUS -> uiState.focusRemainingSeconds
        FocusPhase.BREAK -> uiState.breakRemainingSeconds
    }
    // Type: Float 0f..1f | phase-specific progress for CircularProgressIndicator
    val progress = when (phase) {
        FocusPhase.FOCUS -> uiState.focusProgress
        FocusPhase.BREAK -> uiState.breakProgress
    }

    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (phase) {
                    FocusPhase.FOCUS -> stringResource(R.string.focus_phase_label)
                    FocusPhase.BREAK -> "Nghỉ ngơi"
                },
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
            )
            when (phase) {
                FocusPhase.FOCUS -> {
                    if (uiState.cycles > 0) {
                        Text(
                            text = stringResource(R.string.focus_cycle, uiState.cycles + 1),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                FocusPhase.BREAK -> {
                    Text(
                        text = "Đã hoàn thành ${uiState.cycles} vòng tập trung",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (uiState.accumulatedBreakSeconds > 0) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Nghỉ tích lũy: +${uiState.accumulatedBreakMinutes} phút",
                            style = MaterialTheme.typography.bodySmall,
                            color = breakColor,
                        )
                    }
                }
            }
        }

        // [Effect] Reset progress animation when phase or cycle changes (FOCUS ↔ BREAK transition)
        key(uiState.cycles, phase) {
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(durationMillis = 800),
                label = "${phase.name.lowercase()}_progress",
            )
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(220.dp),
                    strokeWidth = 10.dp,
                    color = accentColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatTime(remainingSeconds),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = when (phase) {
                            FocusPhase.FOCUS -> if (uiState.isPaused) {
                                stringResource(R.string.countdown_pause)
                            } else {
                                stringResource(R.string.focus_status_running)
                            }
                            FocusPhase.BREAK -> "thời gian nghỉ"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (phase) {
                FocusPhase.FOCUS -> {
                    LargeFloatingActionButton(
                        onClick = { if (uiState.isRunning) onPause() else onResume() },
                    ) {
                        Icon(
                            imageVector = if (uiState.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (uiState.isRunning) {
                                stringResource(R.string.countdown_pause)
                            } else {
                                stringResource(R.string.countdown_resume)
                            },
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
                FocusPhase.BREAK -> {
                    Button(
                        onClick = onSkipBreak,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = breakColor),
                    ) {
                        Text("Tập trung tiếp →")
                    }
                }
            }
            OutlinedButton(
                onClick = onEndClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(stringResource(R.string.focus_end_session))
            }
        }
    }
}

/**
 * [UI — Screen]
 * Shown when timer ends (non-repeat) or user taps "End session".
 * Computes remaining minutes after session; offers mark-done when estimate reaches zero.
 */
@Composable
private fun EndSessionDialog(
    uiState: FocusUiState,
    onContinue: () -> Unit,
    onEnd: (markDone: Boolean) -> Unit,
) {
    val actualMinutes = uiState.actualFocusedMinutes
    val todo = uiState.todo
    val currentRemaining = todo?.remainingMinutes ?: todo?.estimatedMinutes
    val newRemaining = if (currentRemaining != null) maxOf(0, currentRemaining - actualMinutes) else null
    val canMarkDone = newRemaining != null && newRemaining <= 0

    var markDone by remember { mutableStateOf(canMarkDone) }

    AlertDialog(
        onDismissRequest = onContinue,
        title = { Text(stringResource(R.string.focus_end_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.focus_actual_minutes, actualMinutes))
                if (uiState.cycles > 0) {
                    Text(stringResource(R.string.focus_cycles_count, uiState.cycles))
                }
                if (newRemaining != null) {
                    Text(stringResource(R.string.focus_estimated_remaining, newRemaining))
                }
                if (canMarkDone) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = markDone,
                            onCheckedChange = { markDone = it },
                        )
                        Text(stringResource(R.string.focus_mark_task_done))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onEnd(markDone) }) { Text(stringResource(R.string.countdown_end)) }
        },
        dismissButton = {
            TextButton(onClick = onContinue) {
                Text(stringResource(R.string.countdown_back_confirm_continue))
            }
        },
    )
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return String.format("%02d:%02d", m, s)
}
