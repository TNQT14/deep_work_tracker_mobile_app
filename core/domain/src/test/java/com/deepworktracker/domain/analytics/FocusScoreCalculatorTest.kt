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
    ) = FocusSession(
        id = "s",
        goal = "g",
        category = null,
        startTime = Instant.fromEpochMilliseconds(0),
        endTime = if (ended) Instant.fromEpochMilliseconds(total) else null,
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
        val sessions = listOf(session(total = 1000, focused = 800), session(total = 1000, focused = 600))
        assertEquals(0.7f, FocusScoreCalculator.score(sessions), 0.0001f)
    }

    @Test
    fun `ignores active sessions`() {
        val sessions = listOf(session(total = 1000, focused = 500), session(total = 1000, focused = 0, ended = false))
        assertEquals(0.5f, FocusScoreCalculator.score(sessions), 0.0001f)
    }

    @Test
    fun `zero total avoids division by zero`() {
        assertEquals(0f, FocusScoreCalculator.score(listOf(session(total = 0, focused = 0))), 0f)
    }
}