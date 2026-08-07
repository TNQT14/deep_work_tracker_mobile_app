package com.deepworktracker.dashboard.presentation.insights

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.deepworktracker.dashboard.R
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * [UI — Screen] (stateless composable, no ViewModel reference)
 * Horizontally-paged carousel of insight cards, shown at the top of the Dashboard.
 * Swiping a card in any direction dismisses it via [onDismiss].
 *
 * Input: insights (List<Insight>, e.g. [Insight(type=BEST_TIME_WINDOW, ...)]),
 *        onDismiss (String -> Unit, called with the swiped insight's id)
 * Process: early-returns (renders nothing) when insights is empty — the caller
 *          (DashboardScreen) also guards with `if (uiState.insights.isNotEmpty())`,
 *          this is a defensive second check. Each page wraps one InsightCard in a
 *          SwipeToDismissBox; any swipe direction (Settled excluded) fires onDismiss
 *          then reports back `true` to let Material3 animate the dismissal.
 * Output: renders 0 or N horizontally swipeable cards. Does not mutate its own state —
 *         the actual removal happens up in DashboardViewModel (Room delete -> Flow
 *         re-emits a shorter list -> this composable recomposes with fewer pages).
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InsightCarousel(
    insights: List<Insight>,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (insights.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { insights.size })

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 8.dp),
        pageSpacing = 12.dp,
        modifier = modifier.fillMaxWidth(),
    ) { page ->
        val insight = insights[page]
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it != SwipeToDismissBoxValue.Settled) {
                    onDismiss(insight.id)
                    true
                } else {
                    false
                }
            },
        )
        SwipeToDismissBox(state = dismissState, backgroundContent = {}) {
            InsightCard(insight)
        }
    }
}

@Composable
private fun InsightCard(insight: Insight) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = insightTitle(insight.type),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = insightMessage(insight),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

/**
 * [UI] i18n label per InsightType. Static per-type strings, no `data` needed —
 * see insightMessage() below for the parameterized body text.
 */
@Composable
private fun insightTitle(type: InsightType): String = when (type) {
    InsightType.BEST_TIME_WINDOW -> stringResource(R.string.insight_best_time_title)
    InsightType.DISTRACTION_PATTERN -> stringResource(R.string.insight_distraction_title)
    InsightType.OPTIMAL_SESSION_LENGTH -> stringResource(R.string.insight_optimal_title)
    InsightType.PRODUCTIVITY_TREND -> stringResource(R.string.insight_trend_title)
}

/**
 * [UI]
 * Input: insight — reads insight.type + insight.data (Map<String, Any>?)
 * Process: builds the localized body text by pulling parameters straight out of
 *          `data` (e.g. data["hours"], data["dayOfWeek"]) into a stringResource
 *          placeholder (%1$s, %2$s in values/strings.xml + values-vi/strings.xml).
 *          Falls back to insight.message (English-only, non-localized) if `data`
 *          is null — should not normally happen since every rule always populates it.
 * Output: localized String, e.g. "Bạn tập trung tốt nhất khoảng 9,10 giờ" (vi) or
 *         "You focus best around 9,10 h" (en).
 */
@Composable
private fun insightMessage(insight: Insight): String {
    val data = insight.data ?: return insight.message
    return when (insight.type) {
        InsightType.BEST_TIME_WINDOW ->
            stringResource(R.string.insight_best_time_msg, data["hours"].toString())
        InsightType.DISTRACTION_PATTERN ->
            stringResource(R.string.insight_distraction_msg, dayLabel(data["dayOfWeek"]), data["ratio"].toString())
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