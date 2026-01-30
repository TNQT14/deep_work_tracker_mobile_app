package com.deepworktracker.dashboard.presentation.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepworktracker.common.time.TimeFormatter
import com.deepworktracker.domain.model.FocusSession
import kotlin.time.Duration.Companion.milliseconds

/**
 * Pie Chart hiển thị phân bố focus time theo loại goal
 * Vico không có Pie Chart built-in, nên dùng Canvas hoặc thư viện khác
 * Hoặc dùng simple implementation với progress indicators
 */
@Composable
fun GoalDistributionChart(
    sessions: List<FocusSession>,
    onGoalClick: (String) -> Unit = {}
) {
    // Tính phân bố theo goal
    val goalDistribution = remember(sessions) {
        if (sessions.isEmpty()) {
            emptyList()
        } else {
            val totalTime = sessions.sumOf { it.totalDuration }
            sessions.groupBy { it.goal }
                .map { (goal, sessionList) ->
                    val time = sessionList.sumOf { it.totalDuration }
                    val percentage = (time.toFloat() / totalTime * 100).toInt()
                    GoalData(
                        goal = goal,
                        duration = time,
                        percentage = percentage,
                        color = getColorForGoal(goal)
                    )
                }
                .sortedByDescending { it.duration }
                .take(5) // Top 5 goals
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Focus Distribution by Goal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Top activities by time spent",
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    goalDistribution.forEach { goalData ->
                        GoalDistributionRow(
                            goalData = goalData,
                            onClick = { onGoalClick(goalData.goal) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalDistributionRow(goalData: GoalData, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Color indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(goalData.color, CircleShape)
                )

                Text(
                    text = goalData.goal,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = TimeFormatter.formatDurationShort(goalData.duration.milliseconds),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${goalData.percentage}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // Progress bar
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = goalData.percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = goalData.color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

data class GoalData(
    val goal: String,
    val duration: Long,
    val percentage: Int,
    val color: Color
)

// Helper function để assign color cho mỗi goal
fun getColorForGoal(goal: String): Color {
    val colors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6), // Purple
        Color(0xFFEC4899), // Pink
        Color(0xFFF59E0B), // Amber
        Color(0xFF10B981), // Green
        Color(0xFF3B82F6), // Blue
        Color(0xFFF97316), // Orange
        Color(0xFF14B8A6)  // Teal
    )

    // Hash goal string để có color consistent
    val index = goal.hashCode().let { if (it < 0) -it else it } % colors.size
    return colors[index]
}