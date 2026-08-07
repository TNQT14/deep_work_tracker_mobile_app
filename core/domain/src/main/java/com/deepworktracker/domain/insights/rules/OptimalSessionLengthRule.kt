package com.deepworktracker.domain.insights.rules

import com.deepworktracker.domain.insights.InsightRule
import com.deepworktracker.domain.insights.StatsWindow
import com.deepworktracker.domain.insights.buildInsight
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import kotlin.math.roundToInt

class OptimalSessionLengthRule : InsightRule {
    override fun evaluate(window: StatsWindow): Insight? {
        val completed = window.sessions.filter { it.endTime != null }
        val buckets = completed.groupBy { it.focusMinutes }
            .filterValues { it.size >= MIN_SAMPLE_PER_BUCKET }
        if (buckets.size < 2) return null
        val scored = buckets.mapValues { (_, list) ->
            val totalFocus = list.sumOf { it.focusedDuration }
            val totalDuration = list.sumOf { it.totalDuration }
            if (totalDuration <= 0L) 0f else totalFocus.toFloat() / totalDuration
        }

        val best = scored.maxByOrNull { it.value } ?: return null
        if(best.value <= 0f) return null

        return buildInsight(
            type = InsightType.OPTIMAL_SESSION_LENGTH,
            fallbackMessage = "${best.key}-min sessions give your best focus",
            now = window.now,
            data = mapOf(
                "bucketMinutes" to best.key,
                "score" to (best.value * 100).roundToInt() / 100.0,
            ),
        )
    }

    companion object {
        const val MIN_SAMPLE_PER_BUCKET = 3
    }
}