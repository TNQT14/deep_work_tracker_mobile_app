package com.deepworktracker.session.domain.interruption

import com.deepworktracker.domain.model.Interruption

/**
 * [Logic]
 * Pure function (roadmap #1, M1.3): focusedDuration = totalDuration − Σ interruption.duration,
 * counting only interruptions that were closed (endTime != null) before the session ended.
 * A still-open interruption is closed by [InterruptionDetector] via SessionEnded before
 * EndSessionUseCase runs, so this is defensive rather than the expected path.
 */
fun calculateFocusedDuration(totalDurationMs: Long, interruptions: List<Interruption>): Long {
    val interruptedMs = interruptions
        .filter { it.endTime != null }
        .sumOf { it.duration }
    return (totalDurationMs - interruptedMs).coerceAtLeast(0)
}
