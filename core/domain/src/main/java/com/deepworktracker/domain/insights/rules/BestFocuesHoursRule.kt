package com.deepworktracker.domain.insights.rules

import com.deepworktracker.domain.insights.InsightRule
import com.deepworktracker.domain.insights.StatsWindow
import com.deepworktracker.domain.insights.buildInsight
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType

/**
 * [Domain — Insight Rule] "Your best focus hours are ..."
 * Surfaces the hours-of-day where the user historically completes the most focused
 * minutes. No calculation happens here — FocusHeatmapAggregator (feature #6) already
 * ranks the top hours by total focusedDuration; this rule just checks whether that
 * result is non-empty and wraps it as an Insight.
 */
class BestFocusHoursRule : InsightRule {

    /**
     * Input: window.current.bestFocusHours — List<Int> of hour-of-day (0-23),
     *        e.g. [9, 10, 21]
     * Process: bail out (return null) if the list is empty — happens when there is no
     *          completed session data for the period, even if hasEnoughData passed.
     * Output: Insight(type=BEST_TIME_WINDOW, data={"hours": "9,10,21", "count": 3})
     *         or null.
     */
    override fun evaluate(window: StatsWindow): Insight? {
        val hours = window.current.bestFocusHours
        if (hours.isEmpty()) return null

        return buildInsight(
            type = InsightType.BEST_TIME_WINDOW,
            fallbackMessage = "You focus best around ${hours.joinToString()}h",
            now = window.now,
            data = mapOf(
                "hours" to hours.joinToString(","), "count" to hours.size
            )
        )
    }
}