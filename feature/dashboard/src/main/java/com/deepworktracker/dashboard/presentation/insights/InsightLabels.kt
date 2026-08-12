package com.deepworktracker.dashboard.presentation.insights

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.deepworktracker.dashboard.R
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
internal fun insightTitle(type: InsightType): String = when (type) {
    InsightType.BEST_TIME_WINDOW -> stringResource(R.string.insight_best_time_title)
    InsightType.DISTRACTION_PATTERN -> stringResource(R.string.insight_distraction_title)
    InsightType.OPTIMAL_SESSION_LENGTH -> stringResource(R.string.insight_optimal_title)
    InsightType.PRODUCTIVITY_TREND -> stringResource(R.string.insight_trend_title)
}

@Composable
internal fun insightMessage(insight: Insight): String {
    val data = insight.data ?: return insight.message
    return when (insight.type) {
        InsightType.BEST_TIME_WINDOW ->
            stringResource(R.string.insight_best_time_msg, data["hours"].toString())
        InsightType.DISTRACTION_PATTERN ->
            stringResource(
                R.string.insight_distraction_msg,
                dayLabel(data["dayOfWeek"]),
                data["ratio"].toString(),
            )
        InsightType.OPTIMAL_SESSION_LENGTH ->
            stringResource(R.string.insight_optimal_msg, data["bucketMinutes"].toString())
        InsightType.PRODUCTIVITY_TREND ->
            stringResource(R.string.insight_trend_msg, percentDrop(data["delta"]))
    }
}

@Composable
private fun dayLabel(value: Any?): String {
    val index = (value as? Number)?.toInt() ?: return "?"
    val ids = intArrayOf(
        R.string.insight_day_mon, R.string.insight_day_tue, R.string.insight_day_wed,
        R.string.insight_day_thu, R.string.insight_day_fri, R.string.insight_day_sat,
        R.string.insight_day_sun,
    )
    return stringResource(ids[(index - 1).coerceIn(0, 6)])
}

private fun percentDrop(value: Any?): String {
    val delta = (value as? Number)?.toDouble() ?: 0.0
    return "${(abs(delta) * 100).roundToInt()}%"
}
