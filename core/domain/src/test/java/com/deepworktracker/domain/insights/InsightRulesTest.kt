package com.deepworktracker.domain.insights

import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.analytics.FocusAnalytics
import com.deepworktracker.domain.insights.rules.BestFocusHoursRule
import com.deepworktracker.domain.insights.rules.DecliningTrendRule
import com.deepworktracker.domain.insights.rules.DistractionPatternRule
import com.deepworktracker.domain.insights.rules.OptimalSessionLengthRule
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.InsightType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
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
    private fun analytics(score: Float, sessions: Int = 10, bestFocusHours: List<Int> = emptyList()) =
        FocusAnalytics.empty(AnalyticsPeriod.WEEK)
            .copy(focusScore = score, sessionCount = sessions, bestFocusHours = bestFocusHours)

    // Test fixture: builds a minimal StatsWindow for a single rule under test.
    private fun window(
        current: FocusAnalytics,
        previous: FocusAnalytics? = null,
        byDay: Map<Int, Int> = emptyMap(),
        sessions: List<FocusSession> = emptyList(),
    ) = StatsWindow(
        period = AnalyticsPeriod.WEEK,
        current = current,
        previous = previous,
        sessions = sessions,
        interruptionsByDay = byDay,
        now = Clock.System.now(),
    )

    // Test fixture: a completed FocusSession with a given planned length (focusMinutes)
    // and focus ratio (focused/total) — matches OptimalSessionLengthRule's bucketing key.
    private fun session(focusMinutes: Int, totalMs: Long, focusedMs: Long) = FocusSession(
        id = "s-$focusMinutes-${totalMs}",
        goal = "g",
        category = null,
        startTime = Instant.fromEpochMilliseconds(0),
        endTime = Instant.fromEpochMilliseconds(totalMs),
        totalDuration = totalMs,
        focusedDuration = focusedMs,
        tag = null,
        note = null,
        focusMinutes = focusMinutes,
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

    // ---- BestFocusHoursRule ----

    @Test
    fun `bestFocusHours phat khi current co gio vang`() {
        val insight = BestFocusHoursRule().evaluate(
            window(current = analytics(0.6f, bestFocusHours = listOf(9, 10))),
        )
        assertNotNull(insight)
        assertEquals(InsightType.BEST_TIME_WINDOW, insight!!.type)
        assertNull(insight.confidence)
        assertEquals("9,10", insight.data!!["hours"])
    }

    @Test
    fun `bestFocusHours khong phat khi rong`() {
        assertNull(
            BestFocusHoursRule().evaluate(
                window(current = analytics(0.6f, bestFocusHours = emptyList())),
            ),
        )
    }

    // ---- OptimalSessionLengthRule ----

    @Test
    fun `optimal phat va chon bucket diem cao hon`() {
        val sessions = List(3) { session(focusMinutes = 25, totalMs = 1_500_000, focusedMs = 450_000) } + // score 0.3
            List(3) { session(focusMinutes = 50, totalMs = 3_000_000, focusedMs = 1_800_000) } // score 0.6

        val insight = OptimalSessionLengthRule().evaluate(
            window(current = analytics(0.5f), sessions = sessions),
        )
        assertNotNull(insight)
        assertEquals(InsightType.OPTIMAL_SESSION_LENGTH, insight!!.type)
        assertEquals(50, insight.data!!["bucketMinutes"])
        assertEquals(0.6, insight.data!!["score"] as Double, 0.01)
    }

    @Test
    fun `optimal khong phat khi chi co 1 bucket`() {
        val sessions = List(5) { session(focusMinutes = 25, totalMs = 1_500_000, focusedMs = 750_000) }
        assertNull(
            OptimalSessionLengthRule().evaluate(window(current = analytics(0.5f), sessions = sessions)),
        )
    }

    @Test
    fun `optimal khong phat khi bucket chua du mau`() {
        // Mỗi bucket chỉ 2 phiên < MIN_SAMPLE_PER_BUCKET=3 -> cả 2 bucket bị lọc, sessions = null.
        val sessions = List(2) { session(focusMinutes = 25, totalMs = 1_500_000, focusedMs = 450_000) } +
            List(2) { session(focusMinutes = 50, totalMs = 3_000_000, focusedMs = 1_800_000) }
        assertNull(
            OptimalSessionLengthRule().evaluate(window(current = analytics(0.5f), sessions = sessions)),
        )
    }
}