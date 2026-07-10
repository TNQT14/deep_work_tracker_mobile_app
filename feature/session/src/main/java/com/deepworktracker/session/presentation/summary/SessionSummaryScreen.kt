package com.deepworktracker.session.presentation.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.common.time.TimeFormatter
import com.deepworktracker.domain.model.Interruption
import com.deepworktracker.domain.model.InterruptionType
import com.deepworktracker.session.R
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSummaryScreen(
    viewModel: SessionSummaryViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.session_summary_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.session_summary_done_action))
                        }
                    },
                )
            },
        ) { padding ->
            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            val session = uiState.session
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = padding.calculateTopPadding()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatCard(
                            label = stringResource(R.string.session_summary_total_label),
                            value = TimeFormatter.formatDuration((session?.totalDuration ?: 0L).milliseconds),
                            modifier = Modifier.weight(1f),
                        )
                        StatCard(
                            label = stringResource(R.string.session_summary_focused_label),
                            value = TimeFormatter.formatDuration((session?.focusedDuration ?: 0L).milliseconds),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.session_summary_interruptions_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                if (uiState.interruptions.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.session_summary_no_interruptions),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                    }
                } else {
                    items(uiState.interruptions) { interruption ->
                        InterruptionRow(interruption)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun InterruptionRow(interruption: Interruption) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = interruption.type.labelRes().let { stringResource(it) })
            Text(
                text = TimeFormatter.formatDuration(interruption.duration.milliseconds),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private fun InterruptionType.labelRes(): Int = when (this) {
    InterruptionType.SCREEN_LOCK -> R.string.session_summary_interruption_type_screen_lock
    InterruptionType.APP_SWITCH -> R.string.session_summary_interruption_type_app_switch
    InterruptionType.BACKGROUND -> R.string.session_summary_interruption_type_background
}
