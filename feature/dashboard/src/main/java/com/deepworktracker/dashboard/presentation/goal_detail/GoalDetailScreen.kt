package com.deepworktracker.dashboard.presentation.goal_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepworktracker.common.time.TimeFormatter
import com.deepworktracker.dashboard.presentation.chart.AggregateBarChart
import com.deepworktracker.dashboard.presentation.dashboard.SessionCard
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.datetime.LocalDate

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
                    Text(
                        text = "Total: ${TimeFormatter.formatDurationShort(uiState.totalDuration.milliseconds)}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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
                            valuesMinutes = uiState.chartByDay.map { it.second / 3600_000 }
                        )

                        1 -> AggregateBarChart(
                            title = "Focus theo tuần",
                            subtitle = "Phút focus theo từng tuần trong ngày",
                            valuesMinutes = uiState.chartByHour.map { it / 60_000 }
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