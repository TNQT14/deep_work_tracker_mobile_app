package com.deepworktracker.session.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.deepworktracker.domain.model.Interruption
import com.deepworktracker.domain.repository.InterruptionRepository
import com.deepworktracker.session.domain.interruption.InterruptionStateMachine
import com.deepworktracker.session.domain.interruption.InterruptionStateMachine.Command
import com.deepworktracker.session.domain.interruption.InterruptionStateMachine.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [Effect] [DI]
 * Android glue for interruption detection (roadmap #1, M1.2). Observes the two signals
 * that mean "the user left the focus session" and feeds them, stamped with the time they
 * occurred, into the pure [InterruptionStateMachine]:
 *
 *  - [ProcessLifecycleOwner] onStop/onStart → app backgrounded/foregrounded
 *  - dynamic [BroadcastReceiver] for ACTION_SCREEN_OFF (can't be declared in the manifest)
 *
 * The state machine decides open/close/reclassify; this class persists the result through
 * [InterruptionRepository]. Events are funnelled through a single [Channel] so the machine
 * and the active-interruption bookkeeping are only ever touched by one coroutine (no races
 * between the main-thread callbacks).
 *
 * Owned and driven by [FocusSessionService]; there is only ever one active session so the
 * singleton holds at most one machine at a time.
 */
@Singleton
class InterruptionDetector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val interruptionRepository: InterruptionRepository,
    private val notificationHelper: SessionNotificationHelper,
) {
    /** DB writes + serialized event processing live here for the detector's lifetime. */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** ProcessLifecycleOwner + receiver registration must happen on the main thread. */
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _interruptionCount = MutableStateFlow(0)

    /** Number of interruptions opened in the current session — drives the notification. */
    val interruptionCount: StateFlow<Int> = _interruptionCount.asStateFlow()

    private var sessionId: String? = null
    private var events: Channel<Event>? = null
    private var processorJob: Job? = null

    /** The single open interruption, if any — mutated only inside the processor coroutine. */
    private var active: Interruption? = null

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                emit(Event.ScreenOff(Clock.System.now()))
            }
        }
    }

    /** Called by ForegroundAppMonitor with a sampled foreground package (already blocklist-filtered). */
    fun reportForegroundPackage(packageName: String) {
        emit(
            Event.DistractionDetected(Clock.System.now(), packageName)
        )
    }

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) = emit(Event.AppBackgrounded(Clock.System.now()))
        override fun onStart(owner: LifecycleOwner) =
            emit(Event.AppForegrounded(Clock.System.now()))
    }

    /**
     * [Effect]
     * Begin detecting for [sessionId]. Idempotent per session. Safe to call from any thread —
     * the [ProcessLifecycleOwner]/receiver registration is posted to the main thread.
     */
    fun start(sessionId: String) {
        if (this.sessionId == sessionId) return
        // Different/first session: tear down any previous wiring first.
        if (this.sessionId != null) stop()

        this.sessionId = sessionId
        val stateMachine = InterruptionStateMachine()
        active = null
        _interruptionCount.value = 0

        val channel = Channel<Event>(Channel.UNLIMITED)
        events = channel
        // Capture sessionId + machine as locals so a pending stop() nulling the shared
        // fields can't stop this coroutine from draining the final SessionEnded event.
        processorJob = scope.launch {
            for (event in channel) handle(event, sessionId, stateMachine)
        }

        mainHandler.post {
            val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
            ContextCompat.registerReceiver(
                context, screenReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED,
            )
            val lifecycle = ProcessLifecycleOwner.get().lifecycle
            lifecycle.addObserver(lifecycleObserver)

            // Service (re)started by START_STICKY while the app is already backgrounded:
            // the user is away for a reason we didn't observe (e.g. process death) → BACKGROUND.
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                emit(Event.StartedInBackground(Clock.System.now()))
            }
        }
    }

    /**
     * [Effect]
     * Stop detecting. Unregisters the observers (on the main thread), then drains any queued
     * events and closes a still-open interruption (SessionEnded) before the processor
     * coroutine finishes. Safe to call from any thread, and when not started.
     */
    fun stop() {
        if (sessionId == null) return
        mainHandler.post {
            runCatching { context.unregisterReceiver(screenReceiver) }
            ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
        }

        // Close a dangling interruption then let the processor loop terminate.
        emit(Event.SessionEnded(Clock.System.now()))
        events?.close()

        sessionId = null
        events = null
        processorJob = null
    }

    private fun emit(event: Event) {
        events?.trySend(event)
    }

    private suspend fun handle(
        event: Event,
        currentSession: String,
        machine: InterruptionStateMachine
    ) {
        when (val command = machine.onEvent(event)) {
            is Command.Open -> {
                val interruption = Interruption(
                    id = UUID.randomUUID().toString(),
                    sessionId = currentSession,
                    startTime = command.at,
                    endTime = null,
                    type = command.type,
                    duration = 0,
                )
                interruptionRepository.saveInterruption(interruption)
                active = interruption
                _interruptionCount.value += 1
            }

            is Command.UpdateType -> {
                active?.let { current ->
                    val updated = current.copy(type = command.type)
                    interruptionRepository.updateInterruption(updated)
                    active = updated
                }
            }

            is Command.Close -> {
                active?.let { current ->
                    val duration =
                        (command.at - current.startTime).inWholeMilliseconds.coerceAtLeast(0)
                    val closed = current.copy(endTime = command.at, duration = duration)
                    interruptionRepository.updateInterruption(closed)
                    active = null
                }
            }

            Command.None -> Unit
            is Command.SetDistraction -> {
                active?.let { current ->
                    val updated = current.copy(distractionPackage = command.packageName)
                    interruptionRepository.updateInterruption(updated)
                    active = updated
                    notificationHelper.notifyDistraction(command.packageName)
                }
            }
        }
    }
}
