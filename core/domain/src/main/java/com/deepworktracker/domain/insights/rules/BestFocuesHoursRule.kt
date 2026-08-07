package com.deepworktracker.domain.insights.rules

import com.deepworktracker.domain.insights.InsightRule
import com.deepworktracker.domain.insights.StatsWindow
import com.deepworktracker.domain.insights.buildInsight
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType

class BestFocusHoursRule : InsightRule {
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