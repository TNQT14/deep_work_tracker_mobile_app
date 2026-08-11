package com.deepworktracker.dashboard.presentation.dashboard.streak

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepworktracker.dashboard.R
import com.deepworktracker.domain.streak.StreakResult

/**
 * [UI] Huy hiệu 🔥 + số ngày, đặt trong TopAppBar actions.
 * Ẩn hoàn toàn khi chưa có chuỗi — tránh hiển thị "0 ngày" cho user mới,
 * vốn là trải nghiệm khởi đầu tiêu cực.
 * Mờ đi khi hôm nay CHƯA đạt goal (nhưng con số KHÔNG đổi — chuỗi chưa gãy).
 */
@Composable
fun StreakBadge(
    streak: StreakResult,
    modifier: Modifier = Modifier,
) {
    if (streak.current <= 0) return

    val label = stringResource(R.string.dashboard_streak_days, streak.current)
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .semantics { contentDescription = label },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "🔥",
            modifier = Modifier.alpha(if (streak.isTodayDone) 1f else 0.4f),
        )
        Text(
            text = streak.current.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

/**
 * [UI] Thẻ mục tiêu hôm nay: vòng tiến độ + phút hiện tại/mục tiêu + kỷ lục.
 * Tái dùng pattern FocusScoreRing trong AnalyticsComponents.kt (M3
 * CircularProgressIndicator, không phải Canvas).
 */
@Composable
fun DailyGoalCard(
    streak: StreakResult?,
    goalMinutes: Int,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        // Chưa đặt goal → CTA thay vì hiển thị vòng rỗng vô nghĩa.
        if (goalMinutes <= 0) {
            Text(
                text = stringResource(R.string.dashboard_goal_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
            return@Card
        }

        val todayMinutes = streak?.todayMinutes ?: 0L
        val progress = (todayMinutes.toFloat() / goalMinutes).coerceIn(0f, 1f)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 8.dp,
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.dashboard_goal_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(
                        R.string.dashboard_goal_progress,
                        todayMinutes,
                        goalMinutes,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
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
    }
}