package com.deepworktracker.session.service

/** Pure decisions for auto-DND. No Android deps → JVM unit-testable. */
object DndPolicy {

    sealed interface StartDecision {
        data class Enable(val filterToApply: Int) : StartDecision
        data object NoOp : StartDecision
    }

    sealed interface StopDecision {
        /** Set the system filter back to [filter], then drop our flag. */
        data class Restore(val filter: Int) : StopDecision
        /** User changed DND mid-session — don't stomp, just drop our flag. */
        data object ClearOnly : StopDecision
        /** We weren't holding DND — nothing to do. */
        data object NoOp : StopDecision
    }

    fun onStart(
        dndEnabled: Boolean,
        hasPolicyAccess: Boolean,
        alreadyHolding: Boolean, // previousFilter != null
        applyFilter: Int,
    ): StartDecision =
        if (dndEnabled && hasPolicyAccess && !alreadyHolding) StartDecision.Enable(applyFilter)
        else StartDecision.NoOp

    fun onStop(
        previousFilter: Int?,
        currentSystemFilter: Int,
        appliedFilter: Int,
    ): StopDecision = when {
        previousFilter == null -> StopDecision.NoOp
        currentSystemFilter == appliedFilter -> StopDecision.Restore(previousFilter)
        else -> StopDecision.ClearOnly
    }
}