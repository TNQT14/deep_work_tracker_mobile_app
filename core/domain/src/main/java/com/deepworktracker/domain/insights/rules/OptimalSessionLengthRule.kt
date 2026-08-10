package com.deepworktracker.domain.insights.rules

import com.deepworktracker.domain.insights.InsightRule
import com.deepworktracker.domain.insights.StatsWindow
import com.deepworktracker.domain.insights.buildInsight
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import kotlin.math.roundToInt

/**
 * [Domain — Insight Rule] "N-minute sessions give your best focus"
 * Groups sessions by their planned length (focusMinutes) and compares the focus
 * score of each group, recommending the length bucket that performs best.
 */
class OptimalSessionLengthRule : InsightRule {

    /**
     * Input: window.sessions — List<FocusSession>, e.g. mix of 25-min and 50-min sessions
     * Process:
     *   1. Keep only completed sessions (endTime != null).
     *   2. Bucket by focusMinutes (e.g. {25: [...3 sessions], 50: [...3 sessions]}),
     *      dropping any bucket with fewer than MIN_SAMPLE_PER_BUCKET sessions — a
     *      bucket of 1 session isn't a reliable signal.
     *   3. Guard: need at least 2 buckets, otherwise there's nothing to compare.
     *   4. score per bucket = sum(focusedDuration) / sum(totalDuration), same formula
     *      as FocusScoreCalculator but scoped to one length bucket.
     *   5. Pick the bucket with the highest score.
     * Output: Insight(type=OPTIMAL_SESSION_LENGTH,
     *         data={"bucketMinutes": 50, "score": 0.86}) or null.
     */
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