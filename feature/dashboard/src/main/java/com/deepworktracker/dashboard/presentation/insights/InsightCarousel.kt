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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.ui.theme.tokens.Spacing

/**
 * Horizontally-paged carousel of insight cards (standalone reuse).
 * Dashboard hero embeds the same pattern inside [FocusHeroClusterCard].
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InsightCarousel(
    insights: List<Insight>,
    modifier: Modifier = Modifier,
) {
    if (insights.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { insights.size })

    HorizontalPager(
        state = pagerState,
        key = { insights[it].id },
        contentPadding = PaddingValues(horizontal = Spacing.sm),
        pageSpacing = Spacing.sm + Spacing.xs,
        modifier = modifier.fillMaxWidth(),
    ) { page ->
        InsightCard(insight = insights[page])
    }
}

@Composable
private fun InsightCard(insight: Insight) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = insightTitle(insight.type),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = insightMessage(insight),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
