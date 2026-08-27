package com.deepworktracker.domain.analytics

import com.deepworktracker.domain.model.FocusSession
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class FocusScoreCalculatorTest {

    private fun session(
        total: Long,
        focused: Long,
        ended: Boolean = true,
        startMs: Long = 0L,
        goal: String = "g",
    ) = FocusSession(
        id = "s-$startMs-$goal",
        goal = goal,
        category = null,
        startTime = Instant.fromEpochMilliseconds(startMs),
        endTime = if (ended) Instant.fromEpochMilliseconds(startMs + total) else null,
        totalDuration = total,
        focusedDuration = focused,
        tag = null,
        note = null,
        interruptions = emptyList(),
        todoId = null,
    )

    @Test
    fun `empty list returns zero`() {
        assertEquals(0f, FocusScoreCalculator.score(emptyList()), 0f)
    }

    @Test
    fun `computes focused over total ratio`() {
        val sessions = listOf(
            session(total = 1000, focused = 800, startMs = 0),
            session(total = 1000, focused = 600, startMs = 1000),
        )
        assertEquals(0.7f, FocusScoreCalculator.score(sessions), 0.0001f)
    }

    @Test
    fun `ignores active sessions`() {
        val sessions = listOf(
            session(total = 1000, focused = 500, startMs = 0),
            session(total = 1000, focused = 0, ended = false, startMs = 1000),
        )
        assertEquals(0.5f, FocusScoreCalculator.score(sessions), 0.0001f)
    }

    @Test
    fun `zero total avoids division by zero`() {
        assertEquals(0f, FocusScoreCalculator.score(listOf(session(total = 0, focused = 0))), 0f)
    }

    @Test
    fun `counts short same-goal gap as interruption`() {
        val sessions = listOf(
            session(total = 60_000, focused = 60_000, startMs = 0),
            session(total = 240_000, focused = 240_000, startMs = 120_000),
        )
        assertEquals(60_000L, FocusScoreCalculator.interruptedMs(sessions))
        assertEquals(300_000f / 360_000f, FocusScoreCalculator.score(sessions), 0.0001f)
    }

    @Test
    fun `ignores long gaps and other goals`() {
        val lunch = listOf(
            session(total = 60_000, focused = 60_000, startMs = 0),
            session(total = 60_000, focused = 60_000, startMs = 20 * 60_000L),
        )
        val otherGoal = listOf(
            session(total = 60_000, focused = 60_000, startMs = 0, goal = "A"),
            session(total = 60_000, focused = 60_000, startMs = 120_000, goal = "B"),
        )
        assertEquals(0L, FocusScoreCalculator.interruptedMs(lunch))
        assertEquals(0L, FocusScoreCalculator.interruptedMs(otherGoal))
    }
}
