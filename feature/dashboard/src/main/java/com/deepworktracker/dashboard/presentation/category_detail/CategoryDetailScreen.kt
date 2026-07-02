package com.deepworktracker.dashboard.presentation.category_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.common.time.TimeFormatter
import com.deepworktracker.dashboard.presentation.chart.AggregateBarChart
import com.deepworktracker.dashboard.presentation.dashboard.SessionCard
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    goal: String,
    onBack: () -> Unit = {},
    viewModel: CategoryDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(goal) {
        viewModel.loadCategory(goal)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(goal) },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBack) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tổng thời gian",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = TimeFormatter.formatDurationShort(uiState.totalDuration.milliseconds),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        uiState.compare30d?.let { c ->
                            Spacer(Modifier.height(10.dp))
                            val deltaPct = (c.deltaRatio * 100).roundToInt()
                            Text(
                                text = "30d: ${c.currentMinutes}m (prev ${c.previousMinutes}m, ${if (deltaPct >= 0) "+" else ""}$deltaPct%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            )
                        }
                    }
                }
            }

            uiState.metrics?.let { metrics ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Key metrics (90 ngày)",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "Efficiency ${(metrics.focusEfficiency * 100).roundToInt()}% · Coverage ${(metrics.coverageRatio * 100).roundToInt()}% · Streak ${metrics.currentStreakDays}d (best ${metrics.longestStreakDays}d)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Avg/day ${metrics.avgMinutesPerCalendarDay.roundToInt()}m · Variability CV ${(metrics.dailyCv * 100).roundToInt()} · Peak ${metrics.peakHour?.let { "${it}h" } ?: "—"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                        }
                    }
                }

                item {
                    AggregateBarChart(
                        title = "Nhịp học theo thứ",
                        subtitle = "Tổng phút theo Mon–Sun (90 ngày)",
                        valuesMinutes = metrics.weekdayMinutes
                    )
                }
                item {
                    AggregateBarChart(
                        title = "Độ dài phiên học",
                        subtitle = "Số phiên theo nhóm: 0–15, 15–30, 30–60, 60–90, 90+ phút",
                        valuesMinutes = metrics.sessionLengthHistogram
                    )
                }
            }

            item {
                AggregateBarChart(
                    title = "Focus theo ngày",
                    subtitle = "Phút focus 30 ngày gần nhất",
                    valuesMinutes = uiState.chartByDay.map { it.second / 60_000 }
                )
            }
            item {
                AggregateBarChart(
                    title = "Focus theo tuần",
                    subtitle = "Phút focus theo tuần (12 tuần gần nhất)",
                    valuesMinutes = uiState.chartByWeek.map { it.second / 60_000 }
                )
            }
            item {
                AggregateBarChart(
                    title = "Focus theo tháng",
                    subtitle = "Phút focus theo tháng (12 tháng gần nhất)",
                    valuesMinutes = uiState.chartByMonth.map { it.second / 60_000 }
                )
            }
            item {
                CategoryTasksSection(goal = goal)
            }

            items(uiState.sessions) { session ->
                SessionCard(session = session)
            }
        }

    }
}

