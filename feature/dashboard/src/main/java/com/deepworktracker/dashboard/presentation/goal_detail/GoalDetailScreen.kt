package com.deepworktracker.dashboard.presentation.goal_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.common.time.TimeFormatter
import com.deepworktracker.dashboard.presentation.chart.AggregateBarChart
import com.deepworktracker.dashboard.presentation.dashboard.SessionCard
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.datetime.LocalDate
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goal: String,
    viewModel: GoalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(goal) {
        viewModel.loadGoal(goal)
    }

    Scaffold(topBar = { TopAppBar(title = { Text(goal) }) }) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        } else {
            var selectedSegment by remember { mutableIntStateOf(0) }
            val options = listOf("Theo ngày", "Theo tuần", "Theo tháng", "Theo năm")

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,

                            ) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = "Tổng thời gian",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = TimeFormatter.formatDurationShort(uiState.totalDuration.milliseconds),
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                    }
                }
                item {
                    uiState.metrics?.let { metric -> GoalMetricsSection(
                        metrics = metric,
                    ) }
                }
                item {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        options.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                onClick = { selectedSegment = index },
                                selected = selectedSegment == index,
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    when (selectedSegment) {
                        0 -> AggregateBarChart(
                            title = "Focus theo ngày",
                            subtitle = "Phút focus 30 ngày gần nhất",
                            valuesMinutes = uiState.chartByDay.map { it.second / 60_000 }
                        )

                        1 -> AggregateBarChart(
                            title = "Focus theo tuần",
                            subtitle = "Phút focus theo tuần (12 tuần gần nhất)",
                            valuesMinutes = uiState.chartByWeek.map { it.second / 60_000 }
                        )

                        2 -> AggregateBarChart(
                            title = "Focus theo tháng",
                            subtitle = "Phút focus theo tháng",
                            valuesMinutes = uiState.chartByMonth.map { it.second / 60_000 }
                        )

                        3 -> AggregateBarChart(
                            title = "Focus theo năm",
                            subtitle = "Phút focus theo năm",
                            valuesMinutes = uiState.chartByYear.map { it.second / 60_000 }
                        )
                    }
                }
                items(uiState.sessions) { session ->
                    SessionCard(session = session)
                }
            }
        }
    }
}

//@Composable
@Composable
private fun GoalMetricsSection(
    metrics: GoalMetrics,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Key metrics (90 ngày gần nhất)",
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = " Nhìn nhanh chất lượng tập trung, điều độ và thói quen theo ngày/giờ.",
            style = MaterialTheme.typography.bodySmall,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MetricCard(
                title = "Variability",
                value = "CV ${(metrics.dailyCv * 100).roundToInt()}",
                hint = "Dao động theo ngày",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Peak hour",
                value = metrics.peakHour?.let { "${it}h" } ?: "—",
                hint = metrics.hourEntropy?.let { "Entropy ${(it * 100).roundToInt()}" } ?: "—",
                modifier = Modifier.weight(1f)
            )
        }

        AggregateBarChart(
            title = "Nhịp học theo thứ",
            subtitle = "Tổng phút theo Mon–Sun (90 ngày)",
            valuesMinutes = metrics.weekdayMinutes
        )
        AggregateBarChart(
            title = "Độ dài phiên học",
            subtitle = "Số phiên theo nhóm: 0–15, 15–30, 30–60, 60–90, 90+ phút",
            valuesMinutes = metrics.sessionLengthHistogram
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    hint: String,
    progress: Float? = null,
    emphasize: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (emphasize) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (emphasize) 2.dp else 0.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            if (progress != null) {
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp),
                    color = if (emphasize) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    trackColor = MaterialTheme.colorScheme.surface,
                )
            }
        }
    }
}