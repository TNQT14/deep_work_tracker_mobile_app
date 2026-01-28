package com.deepworktracker.dashboard.presentation.chart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepworktracker.domain.model.FocusSession
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun FocusTimeBarChart(sessions: List<FocusSession>){
    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(sessions) {
        withContext(
            Dispatchers.Default
        ){
            val hourlyData =  sessions.groupBy {
                it.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).hour
            }.mapValues {
                (_, sessionList)-> sessionList.sumOf { it.totalDuration }/60000
            }

            val data = (0..23).map { hour -> hourlyData[hour] ?: 0 }

            modelProducer.tryRunTransaction {
                columnSeries { series(data) }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
    ){
        Column (
            modifier = Modifier.padding(16.dp)
        ){
            Text(
                text = "Focus Time by Hour",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Total minutes of focus per hour",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 16.dp)
            )


            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data to display",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(
                            columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                                rememberLineComponent(
                                    color = MaterialTheme.colorScheme.primary,
                                    thickness = 8.dp
                                )
                            )
                        ),
                        startAxis = rememberStartAxis(
                            label = rememberTextComponent(),
                            tick = rememberLineComponent()
                        ),
                        bottomAxis = rememberBottomAxis(
                            label = rememberTextComponent(),
                            tick = rememberLineComponent()
                        )
                    ),
                    modelProducer = modelProducer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}