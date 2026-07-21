package com.deepworktracker.dashboard.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepworktracker.domain.analytics.AnalyticsPeriod
import kotlin.math.roundToInt

/** Segmented Day / Week / Month selector (roadmap #6, M6.2). */
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
                Text(
                    when (period) {
                        AnalyticsPeriod.DAY -> "Day"
                        AnalyticsPeriod.WEEK -> "Week"
                        AnalyticsPeriod.MONTH -> "Month"
                    }
                )
            }
        }
    }
}

/** Circular focus-score gauge; [score] in 0f..1f rendered as a percentage. */
@Composable
fun FocusScoreRing(
    score: Float,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(120.dp)) {
        CircularProgressIndicator(
            progress = { score.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            strokeWidth = 10.dp,
        )
        Text(
            text = "${(score * 100).roundToInt()}%",
            style = MaterialTheme.typography.headlineSmall,
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

    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Focus heatmap",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Canvas(
                Modifier
                    .fillMaxWidth()
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
    }
}
