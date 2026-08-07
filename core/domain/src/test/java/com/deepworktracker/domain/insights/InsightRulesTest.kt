package com.deepworktracker.domain.insights

import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.analytics.FocusAnalytics
import com.deepworktracker.domain.insights.rules.DecliningTrendRule
import com.deepworktracker.domain.insights.rules.DistractionPatternRule
import com.deepworktracker.domain.model.InsightType
import kotlinx.datetime.Clock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [Domain — Insight Engine tests]
 * Pure unit tests: no Room, no Hilt, no Android dependencies — each rule is a plain
 * function of StatsWindow -> Insight?, so a fake window is enough to drive it.
 */
class InsightRulesTest {

    // Test fixture: builds a FocusAnalytics with only the fields these rules read
    // (focusScore, sessionCount) set; everything else keeps FocusAnalytics.empty()'s
    // defaults. Sample: analytics(0.5f, 10) -> FocusAnalytics(focusScore=0.5, sessionCount=10, ...)
    private fun analytics(score: Float, sessions: Int = 10) =
        FocusAnalytics.empty(AnalyticsPeriod.WEEK).copy(focusScore = score, sessionCount = sessions)

    // Test fixture: builds a minimal StatsWindow for a single rule under test.
    // sessions is always empty here because none of the currently-tested rules
    // (Declining, Distraction) read window.sessions.
    private fun window(
        current: FocusAnalytics,
        previous: FocusAnalytics? = null,
        byDay: Map<Int, Int> = emptyMap(),
    ) = StatsWindow(
        period = AnalyticsPeriod.WEEK,
        current = current,
        previous = previous,
        sessions = emptyList(),
        interruptionsByDay = byDay,
        now = Clock.System.now(),
    )

    @Test
    fun `declining phat khi score duoi 70 phan tram ky truoc`() {
        val insight = DecliningTrendRule().evaluate(
            window(current = analytics(0.5f), previous = analytics(0.8f)),
        )
        assertNotNull(insight)
        assertEquals(InsightType.PRODUCTIVITY_TREND, insight!!.type)
        assertNull(insight.confidence)
        assertTrue((insight.data!!["delta"] as Double) < 0.0)
    }

    @Test
    fun `declining khong phat khi thieu previous`() {
        assertNull(DecliningTrendRule().evaluate(window(current = analytics(0.5f))))
    }

    @Test
    fun `declining khong phat khi previous score bang 0`() {
        assertNull(
            DecliningTrendRule().evaluate(
                window(current = analytics(0.4f), previous = analytics(0f)),
            ),
        )
    }

    @Test
    fun `declining bien dung 70 phan tram thi khong phat`() {
        assertNull(
            DecliningTrendRule().evaluate(
                window(current = analytics(0.56f), previous = analytics(0.8f)),
            ),
        )
    }

    @Test
    fun `distraction phat khi mot ngay vuot troi`() {
        val insight = DistractionPatternRule().evaluate(
            window(current = analytics(0.6f), byDay = mapOf(1 to 1, 2 to 1, 3 to 10)),
        )
        assertNotNull(insight)
        assertEquals(InsightType.DISTRACTION_PATTERN, insight!!.type)
        assertEquals(3, (insight.data!!["dayOfWeek"] as Number).toInt())
    }

    @Test
    fun `distraction khong phat khi thieu so ngay co du lieu`() {
        assertNull(
            DistractionPatternRule().evaluate(
                window(current = analytics(0.6f), byDay = mapOf(3 to 10)),
            ),
        )
    }
}