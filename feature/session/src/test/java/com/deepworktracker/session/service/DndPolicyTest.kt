package com.deepworktracker.session.service

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pure decision tests for [DndPolicy] — no Android runtime needed.
 *
 * [PRIORITY] mirrors NotificationManager.INTERRUPTION_FILTER_PRIORITY (2) and [NONE] mirrors
 * INTERRUPTION_FILTER_NONE (3); using the raw ints keeps the test off android.jar.
 */
class DndPolicyTest {

    private companion object {
        const val ALL = 1       // INTERRUPTION_FILTER_ALL — a typical pre-session filter
        const val PRIORITY = 2  // the filter the app applies
        const val NONE = 3      // user manually switched to something else
    }

    // --- onStart ---------------------------------------------------------------

    @Test
    fun `onStart enables when dnd on, has access, not already holding`() {
        val decision = DndPolicy.onStart(
            dndEnabled = true,
            hasPolicyAccess = true,
            alreadyHolding = false,
            applyFilter = PRIORITY,
        )
        assertEquals(DndPolicy.StartDecision.Enable(PRIORITY), decision)
    }

    @Test
    fun `onStart no-op when dnd disabled`() {
        assertEquals(
            DndPolicy.StartDecision.NoOp,
            DndPolicy.onStart(dndEnabled = false, hasPolicyAccess = true, alreadyHolding = false, applyFilter = PRIORITY),
        )
    }

    @Test
    fun `onStart no-op when policy access missing`() {
        assertEquals(
            DndPolicy.StartDecision.NoOp,
            DndPolicy.onStart(dndEnabled = true, hasPolicyAccess = false, alreadyHolding = false, applyFilter = PRIORITY),
        )
    }

    @Test
    fun `onStart no-op when already holding so the original filter is preserved on sticky restart`() {
        assertEquals(
            DndPolicy.StartDecision.NoOp,
            DndPolicy.onStart(dndEnabled = true, hasPolicyAccess = true, alreadyHolding = true, applyFilter = PRIORITY),
        )
    }

    // --- onStop ----------------------------------------------------------------

    @Test
    fun `onStop no-op when we were not holding dnd`() {
        assertEquals(
            DndPolicy.StopDecision.NoOp,
            DndPolicy.onStop(previousFilter = null, currentSystemFilter = PRIORITY, appliedFilter = PRIORITY),
        )
    }

    @Test
    fun `onStop restores the original filter when system still shows the one we applied`() {
        assertEquals(
            DndPolicy.StopDecision.Restore(ALL),
            DndPolicy.onStop(previousFilter = ALL, currentSystemFilter = PRIORITY, appliedFilter = PRIORITY),
        )
    }

    @Test
    fun `onStop clears only when user changed dnd mid-session`() {
        assertEquals(
            DndPolicy.StopDecision.ClearOnly,
            DndPolicy.onStop(previousFilter = ALL, currentSystemFilter = NONE, appliedFilter = PRIORITY),
        )
    }
}
