package com.deepworktracker.domain.insights.rules

import com.deepworktracker.domain.insights.InsightRule
import com.deepworktracker.domain.insights.StatsWindow
import com.deepworktracker.domain.insights.buildInsight
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import kotlin.math.roundToInt

class DistractionPatternRule : InsightRule {
    override fun evaluate(window: StatsWindow): Insight? {
        val byDay = window.interruptionsByDay.filterValues { it > 0 }
        if (byDay.size <  MIN_DAYS_WITH_DATA) return null

        val avg = byDay.values.sum().toDouble() / byDay.size
        if(avg <= 0 ) return null

        val (peakDay, peakCount) = byDay.maxByOrNull { it.value } ?: return null
        if(peakCount < MIN_ABSOLUTE) return null

        val ratio  = peakCount / avg
        if(ratio < RATIO_THRESHOLD) return null

        return buildInsight(
            type = InsightType.DISTRACTION_PATTERN,
            fallbackMessage = "You get interrupted more on day $peakDay",
            now = window.now,
            data = mapOf(
                "dayOfWeek" to peakDay,
                "ratio" to (ratio * 10).roundToInt() / 10.0,
                "count" to peakCount,
            ),
        )

    }

    companion object {
        const val MIN_DAYS_WITH_DATA = 3
        const val MIN_ABSOLUTE = 3
        const val RATIO_THRESHOLD = 1.8

    }
}