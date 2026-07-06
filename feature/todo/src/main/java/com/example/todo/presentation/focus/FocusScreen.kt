package com.example.todo.presentation.focus

import android.media.RingtoneManager
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.domain.model.AlertMode
import com.deepworktracker.ui.theme.tokens.ComponentColors
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
    // Type: Color | green accent for break phase (design token, not hardcoded)
    val breakColor = ComponentColors.focusBreak
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
                    FocusPhase.BREAK -> stringResource(R.string.focus_break_phase_label)
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
                        text = stringResource(R.string.focus_break_cycles_done, uiState.cycles),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (uiState.accumulatedBreakSeconds > 0) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(
                                R.string.focus_accumulated_break,
                                uiState.accumulatedBreakMinutes,
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = breakColor,
                        )
                    }
                }
            }
        }

        // Type: Animatable<Float> | Sample: value=0.72f mid-sweep; re-created on FOCUS ↔ BREAK switch
        val progressAnimatable = remember(phase) { Animatable(progress) }

        /**
         * [Effect]
         * Input: timerGeneration (bumped by VM on every timer (re)start), isRunning
         * Process: running → snap to current progress then one LinearEasing sweep to 0f
         *          lasting the full remaining duration (no per-tick restart = no stutter);
         *          paused/stopped → snap and freeze at exact position
         * Output: continuous ring motion; MM:SS text still driven by 1s state ticks
         */
        LaunchedEffect(uiState.timerGeneration, uiState.isRunning) {
            if (uiState.isRunning && remainingSeconds > 0) {
                progressAnimatable.snapTo(progress)
                progressAnimatable.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = remainingSeconds * 1000,
                        easing = LinearEasing,
                    ),
                )
            } else {
                progressAnimatable.snapTo(progress)
            }
        }

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progressAnimatable.value },
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
                        FocusPhase.BREAK -> stringResource(R.string.focus_break_time_label)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
                        Text(stringResource(R.string.focus_skip_break))
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

    // Precompute all stat strings unconditionally (composable calls must not be conditional)
    val focusedLabel = stringResource(R.string.focus_summary_focused_label)
    val focusedValue = stringResource(R.string.focus_summary_minutes, actualMinutes)
    val cyclesLabel = stringResource(R.string.focus_summary_cycles_label)
    val cyclesValue = uiState.cycles.toString()
    val breakLabel = stringResource(R.string.focus_summary_break_label)
    val breakValue = stringResource(R.string.focus_summary_break_minutes, uiState.accumulatedBreakMinutes)
    val newRemainingLabel = stringResource(R.string.focus_summary_new_remaining_label)
    val newRemainingValue = newRemaining?.let { stringResource(R.string.focus_summary_minutes, it) }
    val breakColor = ComponentColors.focusBreak

    val stats = buildList {
        add(SummaryStat(focusedLabel, focusedValue, null))
        add(SummaryStat(cyclesLabel, cyclesValue, null))
        if (uiState.accumulatedBreakMinutes > 0) add(SummaryStat(breakLabel, breakValue, breakColor))
        if (newRemainingValue != null) add(SummaryStat(newRemainingLabel, newRemainingValue, null))
    }

    AlertDialog(
        onDismissRequest = onContinue,
        title = { Text(stringResource(R.string.focus_end_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    stats.chunked(2).forEach { rowStats ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            rowStats.forEach { stat ->
                                SummaryStatCell(
                                    modifier = Modifier.weight(1f),
                                    label = stat.label,
                                    value = stat.value,
                                    valueColor = stat.valueColor ?: MaterialTheme.colorScheme.onSurface,
                                )
                            }
                            if (rowStats.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
                if (canMarkDone) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Checkbox(
                            checked = markDone,
                            onCheckedChange = { markDone = it },
                        )
                        Text(stringResource(R.string.focus_mark_task_done))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onEnd(markDone) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
            ) { Text(stringResource(R.string.countdown_end)) }
        },
        dismissButton = {
            TextButton(onClick = onContinue) {
                Text(stringResource(R.string.countdown_back_confirm_continue))
            }
        },
    )
}

/**
 * [UiState]
 * Type: plain data for one stat cell in the end-session summary grid.
 */
private data class SummaryStat(val label: String, val value: String, val valueColor: androidx.compose.ui.graphics.Color?)

/**
 * [UI — Screen]
 * One labelled stat in the EndSessionDialog summary card (label above, value below).
 */
@Composable
private fun SummaryStatCell(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor,
        )
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return String.format("%02d:%02d", m, s)
}
