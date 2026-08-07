package com.deepworktracker.domain.insights

import android.view.Window
import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.analytics.FocusAnalytics
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import kotlinx.datetime.Instant
import java.util.UUID


data class StatsWindow(
    val period: AnalyticsPeriod,
    val current: FocusAnalytics,
    val previous: FocusAnalytics?,
    val sessions: List<FocusSession>,
    val interruptionsByDay: Map<Int, Int>,
    val now: Instant
) {
    val hasEnoughData: Boolean get() = current.sessionCount >= MIN_SESSIONS

    companion object {
        const val MIN_SESSIONS = 5
    }
}

fun interface InsightRule {
    fun evaluate(window: StatsWindow): Insight?
}

internal fun buildInsight(
    type: InsightType,
    fallbackMessage: String,
    now: Instant,
    data: Map<String, Any>
): Insight = Insight(
    id = UUID.randomUUID().toString(),
    type = type,
    message = fallbackMessage,
    generatedAt = now,
    confidence = null,
    data = data
)
