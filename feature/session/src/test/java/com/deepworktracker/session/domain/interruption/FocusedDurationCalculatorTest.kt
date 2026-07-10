package com.deepworktracker.session.domain.interruption

import com.deepworktracker.domain.model.Interruption
import com.deepworktracker.domain.model.InterruptionType
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class FocusedDurationCalculatorTest {

    private val t0 = Instant.fromEpochMilliseconds(1_000_000)

    private fun interruption(startMs: Long, durationMs: Long, closed: Boolean = true) = Interruption(
        id = "i-$startMs",
        sessionId = "session-1",
        startTime = t0 + startMs.milliseconds,
        endTime = if (closed) t0 + (startMs + durationMs).milliseconds else null,
        type = InterruptionType.APP_SWITCH,
        duration = durationMs,
    )

    @Test
    fun `no interruptions returns total duration unchanged`() {
        val result = calculateFocusedDuration(60_000, emptyList())
        assertEquals(60_000, result)
    }

    @Test
    fun `subtracts a single closed interruption`() {
        val result = calculateFocusedDuration(60_000, listOf(interruption(10_000, 5_000)))
        assertEquals(55_000, result)
    }

    @Test
    fun `sums multiple closed interruptions`() {
        val interruptions = listOf(
            interruption(0, 5_000),
            interruption(20_000, 3_000),
            interruption(40_000, 2_000),
        )
        val result = calculateFocusedDuration(60_000, interruptions)
        assertEquals(50_000, result)
    }

    @Test
    fun `excludes still-open interruption from the sum`() {
        val interruptions = listOf(
            interruption(0, 5_000),
            interruption(20_000, 999_000, closed = false),
        )
        val result = calculateFocusedDuration(60_000, interruptions)
        assertEquals(55_000, result)
    }

    @Test
    fun `clamps to zero when interrupted time exceeds total`() {
        val result = calculateFocusedDuration(10_000, listOf(interruption(0, 15_000)))
        assertEquals(0, result)
    }
}
