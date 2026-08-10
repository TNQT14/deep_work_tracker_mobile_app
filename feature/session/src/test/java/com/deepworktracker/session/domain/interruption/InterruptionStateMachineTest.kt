package com.deepworktracker.session.domain.interruption

import com.deepworktracker.domain.model.InterruptionType
import com.deepworktracker.session.domain.interruption.InterruptionStateMachine.Command
import com.deepworktracker.session.domain.interruption.InterruptionStateMachine.Event
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class InterruptionStateMachineTest {

    private val t0 = Instant.fromEpochMilliseconds(1_000_000)
    private fun at(secondsFromT0: Long) = t0 + secondsFromT0.seconds

    @Test
    fun `screen off opens SCREEN_LOCK, foreground closes it`() {
        val sm = InterruptionStateMachine()

        val open = sm.onEvent(Event.ScreenOff(at(0)))
        assertEquals(Command.Open(InterruptionType.SCREEN_LOCK, at(0)), open)
        assertTrue(sm.isInterrupted)

        val close = sm.onEvent(Event.AppForegrounded(at(30)))
        assertEquals(Command.Close(at(30)), close)
        assertFalse(sm.isInterrupted)
    }

    @Test
    fun `app backgrounded with screen on opens APP_SWITCH`() {
        val sm = InterruptionStateMachine()

        val open = sm.onEvent(Event.AppBackgrounded(at(0)))
        assertEquals(Command.Open(InterruptionType.APP_SWITCH, at(0)), open)

        assertEquals(Command.Close(at(10)), sm.onEvent(Event.AppForegrounded(at(10))))
    }

    @Test
    fun `started while app in background opens BACKGROUND`() {
        val sm = InterruptionStateMachine()

        val open = sm.onEvent(Event.StartedInBackground(at(0)))
        assertEquals(Command.Open(InterruptionType.BACKGROUND, at(0)), open)
        assertTrue(sm.isInterrupted)
    }

    @Test
    fun `screen off then background stays a single active interruption`() {
        val sm = InterruptionStateMachine()

        // Real screen lock: SCREEN_OFF fires first and opens the interruption.
        assertEquals(Command.Open(InterruptionType.SCREEN_LOCK, at(0)), sm.onEvent(Event.ScreenOff(at(0))))
        // onStop arrives right after — must NOT open a second interruption.
        assertEquals(Command.None, sm.onEvent(Event.AppBackgrounded(at(0))))
        assertEquals(Command.None, sm.onEvent(Event.AppBackgrounded(at(1))))
    }

    @Test
    fun `late screen off within window reclassifies APP_SWITCH to SCREEN_LOCK`() {
        val sm = InterruptionStateMachine()

        // onStop lands before the SCREEN_OFF broadcast → provisional APP_SWITCH.
        assertEquals(Command.Open(InterruptionType.APP_SWITCH, at(0)), sm.onEvent(Event.AppBackgrounded(at(0))))
        // SCREEN_OFF arrives 1s later (within the 2s window) → reclassify.
        assertEquals(
            Command.UpdateType(InterruptionType.SCREEN_LOCK),
            sm.onEvent(Event.ScreenOff(at(1))),
        )
    }

    @Test
    fun `screen off after reclassify window does not reclassify`() {
        val sm = InterruptionStateMachine()

        assertEquals(Command.Open(InterruptionType.APP_SWITCH, at(0)), sm.onEvent(Event.AppBackgrounded(at(0))))
        // 3s later — outside the 2s window → leave it as APP_SWITCH.
        assertEquals(Command.None, sm.onEvent(Event.ScreenOff(at(3))))
    }

    @Test
    fun `duplicate background while interrupted is ignored`() {
        val sm = InterruptionStateMachine()

        sm.onEvent(Event.AppBackgrounded(at(0)))
        assertEquals(Command.None, sm.onEvent(Event.AppBackgrounded(at(2))))
    }

    @Test
    fun `foreground while focused is ignored`() {
        val sm = InterruptionStateMachine()

        assertEquals(Command.None, sm.onEvent(Event.AppForegrounded(at(0))))
        assertFalse(sm.isInterrupted)
    }

    @Test
    fun `session ended while interrupted closes the interruption`() {
        val sm = InterruptionStateMachine()

        sm.onEvent(Event.ScreenOff(at(0)))
        assertEquals(Command.Close(at(5)), sm.onEvent(Event.SessionEnded(at(5))))
        assertFalse(sm.isInterrupted)
    }

    @Test
    fun `session ended while focused does nothing`() {
        val sm = InterruptionStateMachine()

        assertEquals(Command.None, sm.onEvent(Event.SessionEnded(at(5))))
    }

    @Test
    fun `full cycle can reopen after closing`() {
        val sm = InterruptionStateMachine()

        assertEquals(Command.Open(InterruptionType.APP_SWITCH, at(0)), sm.onEvent(Event.AppBackgrounded(at(0))))
        assertEquals(Command.Close(at(5)), sm.onEvent(Event.AppForegrounded(at(5))))
        // A brand new interruption after returning to focus.
        assertEquals(Command.Open(InterruptionType.SCREEN_LOCK, at(10)), sm.onEvent(Event.ScreenOff(at(10))))
    }

    @Test
    fun `distraction during APP_SWITCH tags the package once`() {
        val sm = InterruptionStateMachine()

        sm.onEvent(Event.AppBackgrounded(at(0))) // opens APP_SWITCH
        assertEquals(
            Command.SetDistraction("com.facebook.katana"),
            sm.onEvent(Event.DistractionDetected(at(6), "com.facebook.katana")),
        )
        // Second sample of the same interruption is throttled.
        assertEquals(
            Command.None,
            sm.onEvent(Event.DistractionDetected(at(11), "com.facebook.katana")),
        )
    }

    @Test
    fun `distraction ignored when not APP_SWITCH`() {
        val sm = InterruptionStateMachine()

        sm.onEvent(Event.ScreenOff(at(0))) // opens SCREEN_LOCK
        assertEquals(
            Command.None,
            sm.onEvent(Event.DistractionDetected(at(6), "com.facebook.katana")),
        )
    }

    @Test
    fun `distraction while focused is ignored`() {
        val sm = InterruptionStateMachine()

        assertEquals(
            Command.None,
            sm.onEvent(Event.DistractionDetected(at(0), "com.facebook.katana")),
        )
    }

    @Test
    fun `distraction tag does not survive reopen`() {
        val sm = InterruptionStateMachine()

        sm.onEvent(Event.AppBackgrounded(at(0)))
        sm.onEvent(Event.DistractionDetected(at(6), "com.facebook.katana"))
        sm.onEvent(Event.AppForegrounded(at(10))) // closes interruption

        // New APP_SWITCH interruption can be tagged again.
        sm.onEvent(Event.AppBackgrounded(at(20)))
        assertEquals(
            Command.SetDistraction("com.instagram.android"),
            sm.onEvent(Event.DistractionDetected(at(26), "com.instagram.android")),
        )
    }
}
