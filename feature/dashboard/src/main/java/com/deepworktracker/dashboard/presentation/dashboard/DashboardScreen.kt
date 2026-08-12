@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.deepworktracker.dashboard.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepworktracker.dashboard.R
import com.deepworktracker.dashboard.presentation.utils.durationWithInterruptionsLabel
import com.deepworktracker.dashboard.presentation.utils.formatSessionTime
import com.deepworktracker.dashboard.presentation.chart.AggregateBarChart
import com.deepworktracker.dashboard.presentation.charts.GoalDistributionChart
import com.deepworktracker.dashboard.presentation.analytics.FocusHeatmap
import com.deepworktracker.dashboard.presentation.analytics.FocusHeroClusterCard
import com.deepworktracker.dashboard.presentation.analytics.PeriodSelector
import com.deepworktracker.dashboard.presentation.dashboard.components.StreakBadge
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.ui.theme.tokens.Spacing
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToSession: () -> Unit = {},
    onNavigateToGoal: (String) -> Unit = {},
    onNavigateToHistory: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                    uiState.streak?.let { StreakBadge(streak = it) }
                }
            )

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
                    contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
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
                    item {
                        PeriodSelector(
                            selected = uiState.selectedPeriod,
                            onSelect = viewModel::onPeriodSelected
                        )
                    }

                    item(key = "hero") {
                        FocusHeroClusterCard(
                            insights = uiState.insights,
                            streak = uiState.streak,
                            dailyGoalMinutes = uiState.dailyGoalMinutes,
                            analytics = uiState.focusAnalytics,
                            onGoalClick = {
                                val today = Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .date
                                onNavigateToHistory(today.toString())
                            },
                        )
                    }

                    uiState.focusAnalytics?.let { analytics ->
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
