package com.deepworktracker.domain.analytics

import com.deepworktracker.domain.model.FocusSession

object FocusScoreCalculator {
    /** Same-goal gaps longer than this are a new sitting, not lost focus. */
    private const val MAX_GAP_MS = 15 * 60_000L

    fun score(sessions: List<FocusSession>): Float {
        val completed = sessions.filter { it.endTime != null }
        val focused = completed.sumOf { it.focusedDuration.coerceAtLeast(0L) }
        val interrupted = interruptedMs(completed)
        val total = focused + interrupted
        if (total <= 0L) return 0f
        val scoreCheck = (focused.toFloat() / total).coerceIn(0f, 1f)
        return (focused.toFloat() / total).coerceIn(0f, 1f)
    }

    fun interruptedMs(sessions: List<FocusSession>): Long {
        val completed = sessions.filter { it.endTime != null }
        val intra = completed.sumOf { (it.totalDuration - it.focusedDuration).coerceAtLeast(0L) }
        val gaps = completed.groupBy { it.todoId ?: it.goal }.values.sumOf { group ->
            group.sortedBy { it.startTime }.zipWithNext { a, b ->
                val end = a.endTime ?: return@zipWithNext 0L
                val gap = (b.startTime - end).inWholeMilliseconds
                if (gap in 1L..MAX_GAP_MS) gap else 0L
            }.sum()
        }
        return intra + gaps
    }
}
