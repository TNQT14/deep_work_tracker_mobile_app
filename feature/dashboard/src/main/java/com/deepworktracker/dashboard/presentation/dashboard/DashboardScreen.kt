@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.deepworktracker.dashboard.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.dashboard.R
import com.deepworktracker.dashboard.presentation.utils.durationWithInterruptionsLabel
import com.deepworktracker.dashboard.presentation.utils.formatSessionTime
import com.deepworktracker.dashboard.presentation.chart.AggregateBarChart
import com.deepworktracker.dashboard.presentation.charts.GoalDistributionChart
import com.deepworktracker.dashboard.presentation.analytics.FocusHeatmap
import com.deepworktracker.dashboard.presentation.analytics.FocusSummaryCard
import com.deepworktracker.dashboard.presentation.analytics.PeriodSelector
import com.deepworktracker.dashboard.presentation.insights.InsightCarousel
import com.deepworktracker.domain.model.FocusSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToSession: () -> Unit = {},
    onNavigateToGoal: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopAppBar(
                    title = { Text(stringResource(R.string.dashboard_title)) },
                    actions = {
                        IconButton(
                            onClick = { viewModel.refresh() },
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(
                                        R.string.dashboard_refresh_content_description,
                                    ),
                                )
                            }
                        }
                        IconButton(onClick = onNavigateToSession) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = stringResource(
                                    R.string.dashboard_session_content_description,
                                ),
                            )
                        }
                    }
                )

                // TEMPORARY DEBUG (M3.3b) — seeds session/interruption data only; go to
                // Background Task Inspector -> generate_insights_periodic -> Run Now to
                // exercise the REAL worker. Delete this button once verified.
                Button(
                    onClick = { viewModel.debugSeedTestData() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                ) {
                    Text("DEBUG: Seed test data")
                }

                if (uiState.isLoading && uiState.focusAnalytics == null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        uiState.error?.let { error ->
                            item(key = "error") {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
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
                                            text = error.message
                                                ?: stringResource(R.string.dashboard_error_generic),
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(onClick = { viewModel.clearError() }) {
                                            Text(stringResource(R.string.dashboard_dismiss))
                                        }
                                    }
                                }
                            }
                        }
                        // [UI — Screen] [UDF: state down] insights carousel — only added
                        // to the LazyColumn when non-empty; onDismiss forwards straight
                        // to the ViewModel event, no local state kept in this composable.
                        if(uiState.insights.isNotEmpty()){
                            item(key = "insight"){
                                InsightCarousel(
                                    insights = uiState.insights,
                                    onDismiss = viewModel::onDissmissInsight
                                )
                            }
                        }

                        item {
                            PeriodSelector(
                                selected = uiState.selectedPeriod,
                                onSelect = viewModel::onPeriodSelected
                            )
                        }

                        uiState.focusAnalytics?.let { analytics ->
                            item {
                                FocusSummaryCard(analytics = analytics)
                            }
                            item {
                                FocusHeatmap(heatmap = analytics.heatmap)
                            }
                            item {
                                AggregateBarChart(
                                    title = stringResource(R.string.dashboard_focus_trend_title),
                                    subtitle = stringResource(R.string.dashboard_focus_trend_subtitle),
                                    valuesMinutes = analytics.dailyTrendMinutes
                                )
                            }
                        }

                        item {
                            GoalDistributionChart(
                                sessions = uiState.allSessions,
                                onGoalClick = onNavigateToGoal
                            )
                        }

                        item {
                            Text(
                                text = stringResource(R.string.dashboard_recent_sessions),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        if (uiState.recentSessions.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.dashboard_no_sessions),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            items(uiState.recentSessions) { session ->
                                SessionCard(session = session)
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun SessionCard(session: FocusSession) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Goal
            Text(
                text = session.goal,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Duration and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = session.durationWithInterruptionsLabel(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = formatSessionTime(session.startTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            // Status badge
            if (session.isActive) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_session_active),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
