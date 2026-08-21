package com.deepworktracker.dashboard.presentation.day_history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.deepworktracker.dashboard.R
import com.deepworktracker.domain.model.FocusSession
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

private const val MINUTES_PER_DAY = 24 * 60
private val CHART_HEIGHT = 96.dp
private val BAR_GAP = 2.dp

@Composable
fun DayHourAxis(
    sessions: List<FocusSession>,
    modifier: Modifier = Modifier,
) {
    val zone = TimeZone.currentSystemDefault()
    val now = remember { Clock.System.now() }
    val cd = stringResource(R.string.day_history_hour_axis_cd)
    val buckets = remember(sessions) { sessions.toHourBuckets(now, zone) }
    val gridLine = MaterialTheme.colorScheme.outlineVariant
    val barColor = MaterialTheme.colorScheme.primary

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .semantics { contentDescription = cd },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.day_history_hour_axis),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                ) {
                Column (modifier = Modifier.height(CHART_HEIGHT),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End,) {
                    listOf( 60, 45, 30, 15, 0).forEach { minutes ->
                        Text(
                            text = minutes.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Column (){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(CHART_HEIGHT)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .drawBehind {
                                val cellH = size.height / GRID_ROWS
                                for (i in 0..GRID_ROWS) {
                                    val y = cellH * i
                                    drawLine(
                                        color = gridLine,
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = 1.dp.toPx(),
                                    )
                                }
                                val colW = size.width / HOURS_PER_DAY
                                val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                                listOf(0, 6, 12, 18, 24).forEach { hour ->
                                    val x = colW * hour
                                    drawLine(
                                        color = gridLine,
                                        start = Offset(x, 0f),
                                        end = Offset(x, size.height),
                                        strokeWidth = 1.dp.toPx(),
                                        pathEffect = dash,
                                    )
                                }

                            },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = BAR_GAP),
                            horizontalArrangement = Arrangement.spacedBy(BAR_GAP),
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            buckets.forEach { bucket ->
                                val fraction = (bucket.totalMin.toFloat() / MINUTES_PER_HOUR)
                                    .coerceIn(0f, 1f)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.BottomCenter,
                                ) {
                                    if (fraction > 0f) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(fraction)
                                                .background(barColor),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = BAR_GAP),
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            buckets.forEach { bucket ->
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (bucket.hour % 6 == 0) {
                                        Text(
                                            text = stringResource(
                                                R.string.day_history_hour_tick,
                                                bucket.hour.toString().padStart(2, '0'),
                                            ),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = stringResource(R.string.day_history_hour_tick, "24"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            modifier = Modifier.align(Alignment.CenterEnd),
                        )
                    }
                }
            }


        }
    }
}

private fun minuteRangeOnDay(
    session: FocusSession,
    now: Instant,
    zone: TimeZone,
): MinuteRange? {
    val startLdt = session.startTime.toLocalDateTime(zone)
    val startMin = startLdt.hour * 60 + startLdt.minute

    val endInstant = session.endTime ?: now
    val endLdt = endInstant.toLocalDateTime(zone)

    val endMinExclusive = when {
        endLdt.date > startLdt.date -> MINUTES_PER_DAY
        endLdt.date < startLdt.date -> return null // dữ liệu lệch — bỏ qua
        else -> (endLdt.hour * 60 + endLdt.minute).coerceIn(0, MINUTES_PER_DAY)
    }

    val clampedStart = startMin.coerceIn(0, MINUTES_PER_DAY - 1)
    val clampedEnd = endMinExclusive.coerceAtLeast(clampedStart + 1)
        .coerceAtMost(MINUTES_PER_DAY)

    return MinuteRange(
        startMin = clampedStart,
        durationMin = clampedEnd - clampedStart,
    )
}

private data class MinuteRange(val startMin: Int, val durationMin: Int)

private const val HOURS_PER_DAY = 24
private const val MINUTES_PER_HOUR = 60
private const val GRID_ROWS = 4

internal data class HourBucket(
    val hour: Int,
    val focusedMin: Int,
    val interruptedMin: Int
) {
    val totalMin: Int get() = (focusedMin + interruptedMin).coerceAtMost(MINUTES_PER_HOUR)
}

internal fun List<FocusSession>.toHourBuckets(
    now: Instant,
    zone: TimeZone,
): List<HourBucket> {
    val focused = IntArray(HOURS_PER_DAY)
    val interrupted = IntArray(HOURS_PER_DAY)
    forEach { session ->
        val range = minuteRangeOnDay(session, now, zone) ?: return@forEach
        val hourMinutes = range.splitIntoHours() // IntArray(24), tổng = durationMin
        val focusedRatio = when {
            session.isActive -> 1f
            session.totalDuration <= 0L -> 1f
            else -> (session.focusedDuration.toFloat() / session.totalDuration).coerceIn(0f, 1f)
        }
        hourMinutes.forEachIndexed { hour, minutesInHour ->
            if (minutesInHour <= 0) return@forEachIndexed
            val focusedChunk =
                (minutesInHour * focusedRatio).roundToInt().coerceAtMost(minutesInHour)
            focused[hour] += focusedChunk
            interrupted[hour] += minutesInHour - focusedChunk
        }
    }
    return List(HOURS_PER_DAY) { hour ->
        HourBucket(
            hour = hour,
            focusedMin = focused[hour].coerceAtMost(MINUTES_PER_HOUR),
            interruptedMin = interrupted[hour].coerceAtMost(
                (MINUTES_PER_HOUR - focused[hour]).coerceAtLeast(0),
            ),
        )
    }
}

private fun MinuteRange.splitIntoHours(): IntArray {
    val hours = IntArray(HOURS_PER_DAY)
    var remaining = durationMin
    var cursor = startMin
    while (remaining > 0 && cursor < MINUTES_PER_DAY) {
        val hour = cursor / MINUTES_PER_HOUR
        val minutesIntoHour = cursor % MINUTES_PER_HOUR
        val chunk = minOf(remaining, MINUTES_PER_HOUR - minutesIntoHour)
        hours[hour] += chunk
        cursor += chunk
        remaining -= chunk
    }
    return hours
}


