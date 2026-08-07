package com.deepworktracker.dashboard.presentation.insights

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
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import com.deepworktracker.ui.theme.DeepWorkTrackerTheme
import kotlinx.datetime.Clock

@Composable
fun InsightCarousel (
    insights: List<Insight>,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    if(insights.isEmpty()) return
    val pagerState = rememberPagerState (pageCount = {
        insights.size
    })

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
fun InsightCard(
    insight: Insight
){
    Card(modifier = Modifier.fillMaxWidth()){
        Column (Modifier.padding(16.dp)){
            Text(
                text = insight.type.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = insight.message,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//private fun InsightCardPreview() {
//    DeepWorkTrackerTheme {
//        InsightCard(
//            insight = Insight(
//                id = "preview",
//                type = InsightType.BEST_TIME_WINDOW,
//                message = "You focus best around 9,10h",
//                generatedAt = Clock.System.now(),
//                confidence = null,
//                data = mapOf("hours" to "9,10"),
//            )
//        )
//    }
//}