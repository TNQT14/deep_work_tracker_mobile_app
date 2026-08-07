package com.deepworktracker.domain.insights.rules

import com.deepworktracker.domain.insights.InsightRule
import com.deepworktracker.domain.insights.StatsWindow
import com.deepworktracker.domain.insights.buildInsight
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import kotlin.math.roundToInt

/**
 * [Domain — Insight Rule] "You get interrupted N.Nx more on day X"
 * Flags the single day-of-week whose interruption count stands out well above the
 * period's average, so the user can spot a recurring distraction pattern
 * (e.g. Wednesdays are always noisy).
 */
class DistractionPatternRule : InsightRule {

    /**
     * Input: window.interruptionsByDay — Map<Int, Int> of isoDayNumber -> count,
     *        e.g. {1: 2, 2: 1, 3: 10}
     * Process:
     *   1. Keep only days that actually have interruptions (byDay).
     *   2. Guard: need at least MIN_DAYS_WITH_DATA distinct days, otherwise "pattern"
     *      would be a claim based on too little spread.
     *   3. avg = mean count across days-with-data (not divided by 7 — an idle day
     *      with 0 interruptions shouldn't dilute the average).
     *   4. peakDay/peakCount = the single worst day.
     *   5. Guard: peakCount must clear an absolute floor (MIN_ABSOLUTE) — 1 interruption
     *      "gấp đôi" trung bình 0.5 would technically ratio-qualify but is noise.
     *   6. ratio = peakCount / avg must clear RATIO_THRESHOLD (1.8x) to count as a
     *      real standout, not just everyday variance.
     * Output: Insight(type=DISTRACTION_PATTERN,
     *         data={"dayOfWeek": 3, "ratio": 2.5, "count": 10}) or null.
     */
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