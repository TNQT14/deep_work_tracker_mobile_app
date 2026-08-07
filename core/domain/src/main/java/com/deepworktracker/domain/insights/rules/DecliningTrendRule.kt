package com.deepworktracker.domain.insights.rules

import com.deepworktracker.domain.insights.InsightRule
import com.deepworktracker.domain.insights.StatsWindow
import com.deepworktracker.domain.insights.buildInsight
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import kotlin.math.roundToInt

class DecliningTrendRule : InsightRule {
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