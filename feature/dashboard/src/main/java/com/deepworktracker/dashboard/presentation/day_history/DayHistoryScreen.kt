package com.deepworktracker.dashboard.presentation.day_history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepworktracker.dashboard.R
import com.deepworktracker.dashboard.presentation.day_history.components.DayHourAxis
import com.deepworktracker.dashboard.presentation.day_history.components.DayStatsGrid
import com.deepworktracker.domain.analytics.FocusScoreCalculator
import com.deepworktracker.domain.model.FocusSession
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayHistoryScreen(
    onBack: () -> Unit,
    onNavigateToGoal: (String) -> Unit,
    viewModel: DayHistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.day_history_title))
                        uiState.date?.let {
                            Text(
                                text = it.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.day_history_back),
                        )
                    }
                },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.groups.isEmpty() -> Text(
                    text = stringResource(R.string.day_history_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(key = "stats") {
                        DayStatsGrid(stats = uiState.stats)
                    }
                    item(key = "hour_axis"){
                        DayHourAxis(sessions = uiState.groups.flatten())
                    }
                    items(uiState.groups, key = { it.first().id }) { groups ->
                        SessionRow(groups = groups, onClick = onNavigateToGoal)
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionRow(groups: SessionGroup, onClick: (String) -> Unit) {
    val first = groups.first()
    val goal = first.goal.takeIf { it.isNotBlank() }

    val done = groups.filter { !it.isActive }
    val focusedMs = done.sumOf { it.focusedDuration }
    val interruptedMs = FocusScoreCalculator.interruptedMs(done)
    val totalMs = focusedMs + interruptedMs
    val focusedMinutes = focusedMs / MILLIS_PER_MINUTE
    val interruptedMinutes = interruptedMs / MILLIS_PER_MINUTE
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (goal != null) Modifier.clickable { onClick(goal) } else Modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = first.goal,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    groups.firstNotNullOfOrNull { it.category?.takeIf { c -> c.isNotBlank() } }
                        ?.let { category ->
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                }

                Text(
                    text = stringResource(
                        R.string.day_history_focused_minutes, focusedMinutes
                    ) + if (interruptedMinutes > 0) {
                        "  ·  " + stringResource(
                            R.string.day_history_interrupted_minutes,
                            interruptedMinutes,
                        )
                    } else "",
                    style = MaterialTheme.typography.bodyMedium,
                )

                if (totalMs > 0) {
                    LinearProgressIndicator(
                        progress = { focusedMs.toFloat() / totalMs },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.error,
                    )
                }

                if (groups.size > 1 || first.isActive) {
                    groups.forEach { session ->
                        Text(
                            text = "${timeRangeLabel(session)}  ·  " + if (session.isActive) {
                                stringResource(R.string.day_history_running)
                            } else {
                                stringResource(
                                    R.string.day_history_focused_minutes,
                                    session.focusedDuration / MILLIS_PER_MINUTE,
                                )
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Text(
                        text = timeRangeLabel(first),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (goal != null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun timeRangeLabel(session: FocusSession): String {
    val start = formatClock(session.startTime)
    val end = session.endTime?.let { formatClock(it) }
        ?: stringResource(R.string.day_history_current)
    return "$start – $end"
}


private fun formatClock(instant: Instant): String {
    val t = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "%02d:%02d".format(t.hour, t.minute)
}

private const val MILLIS_PER_MINUTE = 60_000L