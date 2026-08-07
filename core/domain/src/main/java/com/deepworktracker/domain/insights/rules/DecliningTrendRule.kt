package com.deepworktracker.domain.insights.rules

import com.deepworktracker.domain.insights.InsightRule
import com.deepworktracker.domain.insights.StatsWindow
import com.deepworktracker.domain.insights.buildInsight
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import kotlin.math.roundToInt

/**
 * [Domain — Insight Rule] "Your focus score dropped vs last period"
 * Re-engagement signal: fires when the current period's focus score falls below
 * THRESHOLD (70%) of the previous period's score, catching a churn risk early.
 */
class DecliningTrendRule : InsightRule {

    /**
     * Input: window.current.focusScore (Float 0f..1f), window.previous?.focusScore
     * Process:
     *   1. Guard: no previous period (first period ever tracked) -> null.
     *   2. Guard: previous.focusScore <= 0f -> avoid dividing/multiplying by ~0,
     *      which would make almost any current score look like a "drop".
     *   3. Fire only if current < previous * THRESHOLD (strictly below 70%,
     *      e.g. current=0.56, previous=0.8 -> 0.56 >= 0.56 -> does NOT fire;
     *      current=0.5 -> 0.5 < 0.56 -> fires).
     * Output: Insight(type=PRODUCTIVITY_TREND,
     *         data={"delta": -0.3, "current": 0.5, "previous": 0.8}) or null.
     *         delta is always negative when this rule fires.
     */
    override fun evaluate(window: StatsWindow): Insight? {
        val prev = window.previous ?: return null
        if(prev.focusScore <=0f) return null
        if(window.current.focusScore >= prev.focusScore * THRESHOLD) return null

        val delta = window.current.focusScore - prev.focusScore

        return buildInsight(
            type = InsightType.PRODUCTIVITY_TREND,
            fallbackMessage = "Your focus score dropped vs last period",
            now = window.now,
            data = mapOf(
                "delta" to (delta * 100).roundToInt() / 100.0,
                "current" to (window.current.focusScore * 100).roundToInt() / 100.0,
                "previous" to (prev.focusScore * 100).roundToInt() / 100.0,
            ),
        )
    }

    companion object {
        const val THRESHOLD = 0.7f
    }
}