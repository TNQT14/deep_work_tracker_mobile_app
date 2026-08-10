package com.deepworktracker.dashboard.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.deepworktracker.common.time.TimeFormatter
import com.deepworktracker.dashboard.R
import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.model.FocusSession
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
internal fun AnalyticsPeriod.toDisplayLabel(): String = when (this) {
    AnalyticsPeriod.DAY -> stringResource(R.string.dashboard_period_day)
    AnalyticsPeriod.WEEK -> stringResource(R.string.dashboard_period_week)
    AnalyticsPeriod.MONTH -> stringResource(R.string.dashboard_period_month)
}

@Composable
internal fun formatHourLabel(hour: Int): String = when {
    hour == 0 -> stringResource(R.string.dashboard_hour_midnight)
    hour < 12 -> stringResource(R.string.dashboard_hour_am, hour)
    hour == 12 -> stringResource(R.string.dashboard_hour_noon)
    else -> stringResource(R.string.dashboard_hour_pm, hour - 12)
}

@Composable
internal fun heatmapDayLabels(): List<String> =
    stringArrayResource(R.array.dashboard_heatmap_day_labels).toList()

@Composable
internal fun formatSessionTime(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = localDateTime.hour
    val minute = localDateTime.minute
    return "${formatHourLabel(hour)}:${String.format("%02d", minute)}"
}

@Composable
internal fun FocusSession.durationWithInterruptionsLabel(): String {
    val duration = TimeFormatter.formatDurationShort(totalDuration.milliseconds)
    return pluralStringResource(
        R.plurals.dashboard_session_interruptions,
        interruptions.size,
        duration,
        interruptions.size,
    )
}
