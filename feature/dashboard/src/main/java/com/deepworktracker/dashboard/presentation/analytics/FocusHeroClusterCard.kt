package com.deepworktracker.dashboard.presentation.analytics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.deepworktracker.common.time.TimeFormatter
import com.deepworktracker.dashboard.R
import com.deepworktracker.dashboard.presentation.insights.insightMessage
import com.deepworktracker.dashboard.presentation.insights.insightTitle
import com.deepworktracker.dashboard.presentation.utils.formatHourLabel
import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.analytics.FocusAnalytics
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import com.deepworktracker.domain.streak.StreakResult
import com.deepworktracker.ui.theme.DeepWorkTrackerTheme
import com.deepworktracker.ui.theme.tokens.ComponentColors
import com.deepworktracker.ui.theme.tokens.Spacing
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes

/**
 * Hero cluster order: focus score → today's goal → insight tips (horizontal pager).
 */
@Composable
fun FocusHeroClusterCard(
    insights: List<Insight>,
    streak: StreakResult?,
    dailyGoalMinutes: Int,
    analytics: FocusAnalytics?,
    onGoalClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            if (analytics != null) {
                FocusScoreSection(analytics = analytics)
            }

            GoalSection(
                streak = streak,
                dailyGoalMinutes = dailyGoalMinutes,
                onGoalClick = onGoalClick,
            )

            InsightPager(insights = insights)
        }
    }
}

@Composable
private fun FocusScoreSection(analytics: FocusAnalytics) {
    val score = analytics.focusScore.coerceIn(0f, 1f)
    val scorePercent = (score * 100).roundToInt()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(R.string.dashboard_focus_score),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            FocusScoreGauge(score = score, percentLabel = "$scorePercent%")

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    StatTile(
                        value = TimeFormatter.formatDurationShort(analytics.totalFocusedMinutes.minutes),
                        label = stringResource(R.string.dashboard_stat_focused),
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        value = "${analytics.sessionCount}",
                        label = stringResource(R.string.dashboard_stat_sessions),
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    StatTile(
                        value = TimeFormatter.formatDurationShort(analytics.avgSessionMinutes.minutes),
                        label = stringResource(R.string.dashboard_stat_avg),
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        value = analytics.bestFocusHour?.let { formatHourLabel(it) }
                            ?: stringResource(R.string.dashboard_best_hour_none),
                        label = stringResource(R.string.dashboard_stat_best_hour),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun FocusScoreGauge(
    score: Float,
    percentLabel: String,
    modifier: Modifier = Modifier,
) {
    val ringColor = ComponentColors.percentProgress(score)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(96.dp),
    ) {
        CircularProgressIndicator(
            progress = { score },
            modifier = Modifier.fillMaxSize(),
            color = ringColor,
            strokeWidth = 9.dp,
            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
            strokeCap = StrokeCap.Round,
        )
        Text(
            text = percentLabel,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ringColor,
            maxLines = 1,
        )
    }
}

@Composable
private fun GoalSection(
    streak: StreakResult?,
    dailyGoalMinutes: Int,
    onGoalClick: () -> Unit,
) {
    if (dailyGoalMinutes <= 0) {
        Text(
            text = stringResource(R.string.dashboard_goal_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }

    val todayMinutes = streak?.todayMinutes ?: 0L
    val progress = (todayMinutes.toFloat() / dailyGoalMinutes).coerceIn(0f, 1f)
    val percent = (progress * 100).roundToInt()
    val progressColor = ComponentColors.percentProgress(progress)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onGoalClick)
            .padding(vertical = Spacing.xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_goal_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(
                        R.string.dashboard_goal_progress,
                        todayMinutes,
                        dailyGoalMinutes,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = progressColor,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.dashboard_goal_open),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(percent = 50)),
            color = progressColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
            strokeCap = StrokeCap.Round,
        )

        if (streak != null && streak.longest > 0) {
            Text(
                text = stringResource(R.string.dashboard_goal_best, streak.longest),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InsightPager(insights: List<Insight>) {
    if (insights.isEmpty()) return

    if (insights.size == 1) {
        InsightCallout(
            insight = insights.first(),
            modifier = Modifier.fillMaxWidth(),
        )
        return
    }

    val pagerState = rememberPagerState(pageCount = { insights.size })
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        HorizontalPager(
            state = pagerState,
            key = { insights[it].id },
            pageSpacing = Spacing.sm,
            // Peek next card so swipe affordance is obvious
            contentPadding = PaddingValues(end = Spacing.xl),
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            InsightCallout(
                insight = insights[page],
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(insights.size) { index ->
                val active = pagerState.currentPage == index
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (active) 8.dp else 6.dp),
                    shape = CircleShape,
                    color = if (active) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    },
                ) {}
            }
        }
    }
}

@Composable
private fun InsightCallout(
    insight: Insight,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm + Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = insightTitle(insight.type),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = insightMessage(insight),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun StatTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.sm + Spacing.xs, vertical = Spacing.sm + Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12131A)
@Composable
private fun FocusHeroClusterCardPreview() {
    DeepWorkTrackerTheme(darkTheme = true) {
        FocusHeroClusterCard(
            insights = listOf(
                Insight(
                    id = "1",
                    type = InsightType.DISTRACTION_PATTERN,
                    message = "Friday is your most interrupted day (3.6×)",
                    generatedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(0),
                    data = mapOf("dayOfWeek" to 5, "ratio" to "3.6"),
                ),
                Insight(
                    id = "2",
                    type = InsightType.BEST_TIME_WINDOW,
                    message = "You focus best around 11 h",
                    generatedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(0),
                    data = mapOf("hour" to 11),
                ),
            ),
            streak = StreakResult(
                current = 13,
                longest = 5,
                isTodayDone = false,
                todayMinutes = 65,
            ),
            dailyGoalMinutes = 120,
            analytics = FocusAnalytics(
                period = AnalyticsPeriod.WEEK,
                focusScore = 0.71f,
                totalFocusedMinutes = 4984,
                sessionCount = 135,
                avgSessionMinutes = 36,
                bestFocusHour = 11,
                dailyTrendMinutes = emptyList(),
                heatmap = emptyList(),
                bestFocusHours = listOf(11),
            ),
            onGoalClick = {},
            modifier = Modifier.padding(Spacing.md),
        )
    }
}
