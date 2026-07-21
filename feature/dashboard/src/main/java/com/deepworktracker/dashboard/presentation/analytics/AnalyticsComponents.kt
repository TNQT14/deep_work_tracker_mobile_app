package com.deepworktracker.dashboard.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepworktracker.common.time.TimeFormatter
import com.deepworktracker.dashboard.R
import com.deepworktracker.dashboard.presentation.utils.formatHourLabel
import com.deepworktracker.dashboard.presentation.utils.heatmapDayLabels
import com.deepworktracker.dashboard.presentation.utils.toDisplayLabel
import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.analytics.FocusAnalytics
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes

/** Segmented Day / Week / Month selector (roadmap #6, M6.2). Controls the whole analytics section. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodSelector(
    selected: AnalyticsPeriod,
    onSelect: (AnalyticsPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    val periods = AnalyticsPeriod.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        periods.forEachIndexed { index, period ->
            SegmentedButton(
                selected = period == selected,
                onClick = { onSelect(period) },
                shape = SegmentedButtonDefaults.itemShape(index, periods.size),
            ) {
                Text(period.toDisplayLabel())
            }
        }
    }
}

/** Hero card: focus-score ring + the period's key numbers, all driven by [analytics]. */
@Composable
fun FocusSummaryCard(
    analytics: FocusAnalytics,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FocusScoreRing(score = analytics.focusScore, size = 92.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatItem(
                        label = stringResource(R.string.dashboard_stat_focused),
                        value = TimeFormatter.formatDurationShort(analytics.totalFocusedMinutes.minutes),
                        modifier = Modifier.weight(1f),
                    )
                    StatItem(
                        label = stringResource(R.string.dashboard_stat_sessions),
                        value = "${analytics.sessionCount}",
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatItem(
                        label = stringResource(R.string.dashboard_stat_avg),
                        value = TimeFormatter.formatDurationShort(analytics.avgSessionMinutes.minutes),
                        modifier = Modifier.weight(1f),
                    )
                    StatItem(
                        label = stringResource(R.string.dashboard_stat_best_hour),
                        value = analytics.bestFocusHour?.let { formatHourLabel(it) }
                            ?: stringResource(R.string.dashboard_best_hour_none),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Circular focus-score gauge; [score] in 0f..1f rendered as a percentage. */
@Composable
fun FocusScoreRing(
    score: Float,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 120.dp,
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        CircularProgressIndicator(
            progress = { score.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 8.dp,
        )
        Text(
            text = "${(score * 100).roundToInt()}%",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

/**
 * 7×24 focus heatmap (rows = Mon..Sun, cols = hour 0..23), minutes focused per cell.
 * Cell alpha is normalised against the busiest cell; empty cells use a faint surface tint.
 */
@Composable
fun FocusHeatmap(
    heatmap: List<List<Long>>,
    modifier: Modifier = Modifier,
) {
    val filled = MaterialTheme.colorScheme.primary
    val empty = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val maxVal = (heatmap.flatten().maxOrNull() ?: 0L).coerceAtLeast(1L)
    val dayLabels = heatmapDayLabels()

    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.dashboard_heatmap_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .height(140.dp)
                        .padding(end = 6.dp),
                ) {
                    dayLabels.forEach { label ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                Canvas(
                    Modifier
                        .weight(1f)
                        .height(140.dp)
                ) {
                    val rows = 7
                    val cols = 24
                    val gap = 2.dp.toPx()
                    val cellW = (size.width - gap * (cols - 1)) / cols
                    val cellH = (size.height - gap * (rows - 1)) / rows
                    for (r in 0 until rows) {
                        for (c in 0 until cols) {
                            val v = heatmap.getOrNull(r)?.getOrNull(c) ?: 0L
                            val color = if (v == 0L) {
                                empty
                            } else {
                                val frac = v.toFloat() / maxVal
                                filled.copy(alpha = (0.25f + 0.75f * frac).coerceIn(0f, 1f))
                            }
                            drawRect(
                                color = color,
                                topLeft = Offset(c * (cellW + gap), r * (cellH + gap)),
                                size = Size(cellW, cellH),
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                listOf("0", "6", "12", "18", "23").forEach { hour ->
                    Text(
                        text = hour,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
