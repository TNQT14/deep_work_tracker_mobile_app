package com.deepworktracker.session.domain.interruption

import com.deepworktracker.domain.model.InterruptionType
import com.deepworktracker.session.domain.interruption.InterruptionStateMachine.Command.*
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

/**
 * [Logic]
 * Pure, framework-free state machine that turns raw lifecycle/screen events into
 * interruption commands, enforcing the single-active-interruption invariant:
 *
 *     FOCUSED → INTERRUPTED(type) → FOCUSED
 *
 * Only one interruption is open at a time, matching
 * [com.deepworktracker.domain.repository.InterruptionRepository.getActiveInterruption].
 * All timestamps come from the events (no internal clock) so the machine is fully
 * deterministic and unit-testable.
 *
 * Classification (roadmap #1, M1.2):
 *  - Screen turned off                 → [InterruptionType.SCREEN_LOCK]
 *  - App backgrounded, screen still on → [InterruptionType.APP_SWITCH]
 *  - Detector started while app is
 *    already backgrounded (process-death
 *    restart via START_STICKY)          → [InterruptionType.BACKGROUND]
 *
 * Event-ordering race: on a real screen lock both [Event.ScreenOff] and
 * [Event.AppBackgrounded] fire, order not guaranteed. If [Event.AppBackgrounded]
 * lands first it is provisionally APP_SWITCH; a [Event.ScreenOff] arriving within
 * [RECLASSIFY_WINDOW] then reclassifies the open interruption to SCREEN_LOCK.
 */
class InterruptionStateMachine {

    /** Raw inputs fed from the Android layer, each stamped with when it happened. */
    sealed interface Event {
        val at: Instant

        /** ACTION_SCREEN_OFF broadcast — the display turned off. */
        data class ScreenOff(override val at: Instant) : Event

        /** ProcessLifecycleOwner onStop — the app left the foreground. */
        data class AppBackgrounded(override val at: Instant) : Event

        /** ProcessLifecycleOwner onStart — the app returned to the foreground. */
        data class AppForegrounded(override val at: Instant) : Event

        /** Detector attached while the app process was already in the background. */
        data class StartedInBackground(override val at: Instant) : Event

        /** The focus session ended while an interruption may still be open. */
        data class SessionEnded(override val at: Instant) : Event

        /** Foreground app sampled during an interruption (from UsageStats). */
        data class DistractionDetected(override val at: Instant, val packageName: String) : Event
    }

    /** Instructions for the caller to persist; the machine never touches storage. */
    sealed interface Command {
        /** Open a new interruption of [type] starting at [at]. */
        data class Open(val type: InterruptionType, val at: Instant) : Command

        /** Change the type of the currently-open interruption (reclassification). */
        data class UpdateType(val type: InterruptionType) : Command

        /** Close the currently-open interruption at [at]. */
        data class Close(val at: Instant) : Command

        /** Nothing to persist. */
        data object None : Command

        /** Tag the currently-open interruption with the distracting app package. */
        data class SetDistraction(val packageName: String) : Command
    }

    private sealed interface Phase {
        data object Focused : Phase
        data class Interrupted(
            val type: InterruptionType,
            val startedAt: Instant,
            val distractionPackage: String? = null,
        ) : Phase
    }

    private var phase: Phase = Phase.Focused
    private var lastScreenOffAt: Instant? = null

    /** True while an interruption is open — mirrors getActiveInterruption != null. */
    val isInterrupted: Boolean
        get() = phase is Phase.Interrupted

    /**
     * Input: a single [Event]
     * Process: apply the transition rules, mutate internal phase
     * Output: the [Command] the caller must persist (possibly [Command.None])
     */
    fun onEvent(event: Event): Command = when (val current = phase) {
        Phase.Focused -> onEventWhileFocused(event)
        is Phase.Interrupted -> onEventWhileInterrupted(event, current)
    }

    private fun onEventWhileFocused(event: Event): Command = when (event) {
        is Event.ScreenOff -> {
            lastScreenOffAt = event.at
            open(InterruptionType.SCREEN_LOCK, event.at)
        }

        is Event.AppBackgrounded -> {
            val screenJustOff = lastScreenOffAt
                ?.let { event.at - it <= RECLASSIFY_WINDOW && event.at >= it }
                ?: false
            val type =
                if (screenJustOff) InterruptionType.SCREEN_LOCK else InterruptionType.APP_SWITCH
            open(type, event.at)
        }

        is Event.StartedInBackground -> open(InterruptionType.BACKGROUND, event.at)
        is Event.AppForegrounded -> Command.None
        is Event.SessionEnded -> Command.None
        is Event.DistractionDetected -> Command.None
    }

    private fun onEventWhileInterrupted(event: Event, current: Phase.Interrupted): Command =
        when (event) {
            // A late SCREEN_OFF right after an APP_SWITCH means it was really a screen lock.
            is Event.ScreenOff -> {
                lastScreenOffAt = event.at
                if (current.type == InterruptionType.APP_SWITCH &&
                    event.at - current.startedAt <= RECLASSIFY_WINDOW
                ) {
                    phase = current.copy(type = InterruptionType.SCREEN_LOCK)
                    UpdateType(InterruptionType.SCREEN_LOCK)
                } else {
                    Command.None
                }
            }
            is Event.AppBackgrounded -> Command.None
            is Event.StartedInBackground -> Command.None
            is Event.AppForegrounded -> close(event.at)
            is Event.SessionEnded -> close(event.at)
            is Event.DistractionDetected -> if(current.type == InterruptionType.APP_SWITCH  && current.distractionPackage == null){
                phase = current.copy(
                    distractionPackage = event.packageName
                )
                Command.SetDistraction(event.packageName)
            }
            else{
                Command.None
            }
        }

    private fun open(type: InterruptionType, at: Instant): Command {
        phase = Phase.Interrupted(type, at)
        return Command.Open(type, at)
    }

    private fun close(at: Instant): Command {
        phase = Phase.Focused
        return Command.Close(at)
    }

    companion object {
        /** How long after an APP_SWITCH a SCREEN_OFF still counts as the same screen-lock. */
        val RECLASSIFY_WINDOW = 2.seconds
    }
}
