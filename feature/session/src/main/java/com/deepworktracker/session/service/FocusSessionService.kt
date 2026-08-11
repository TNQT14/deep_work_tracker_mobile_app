package com.deepworktracker.session.service

import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import com.deepworktracker.session.domain.usecase.EndSessionUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration
import javax.inject.Inject

/**
 * [Effect]
 * Foreground service that keeps a running focus session alive with an ongoing timer
 * notification. Survives app kill via START_STICKY: on system restart it re-reads the
 * active session from Room (end_time IS NULL) and resumes the notification, computing
 * elapsed time from the persisted startTime — no extra state needed.
 *
 * M1.1: FGS + timer notification + survives kill + restart recovery.
 * M1.2: drives [InterruptionDetector] for the active session and shows the live
 *       interruption count in the notification.
 * focusedDuration + end-session summary (M1.3) come later.
 */
@AndroidEntryPoint
class FocusSessionService : LifecycleService() {

    @Inject lateinit var sessionRepository: SessionRepository
    @Inject lateinit var endSessionUseCase: EndSessionUseCase
    @Inject lateinit var notificationHelper: SessionNotificationHelper
    @Inject lateinit var ticker: SessionTicker
    @Inject lateinit var interruptionDetector: InterruptionDetector
    @Inject lateinit var dndController: DndController
    @Inject lateinit var foregroundAppMonitor: ForegroundAppMonitor

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var observing = false
    private var tickerJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startForegroundInternal(notificationHelper.build(goal = null, elapsed = Duration.ZERO))

        when (intent?.action) {
            ACTION_END -> endActiveSession()
            ACTION_STOP -> scope.launch {
                dndController.onSessionEnded()
                stopService()
            }
            else -> ensureObserving()
        }
        return START_STICKY
    }

    /** Single collector on the active session: drives ticking and self-stop. */
    private fun ensureObserving() {
        if (observing) return
        observing = true
        scope.launch {
            sessionRepository.observeActiveSession().collect { session ->
                if (session == null) {
                    dndController.onSessionEnded()
                    stopService()
                } else {
                    interruptionDetector.start(session.id)
                    dndController.onSessionStarted()
                    foregroundAppMonitor.start()
                    tickerJob?.cancel()
                    tickerJob = launch {
                        combine(
                            ticker.elapsed(session.startTime),
                            interruptionDetector.interruptionCount,
                        ) { elapsed, count -> elapsed to count }
                            .collect { (elapsed, count) ->
                                notificationHelper.notify(
                                    notificationHelper.build(session.goal, elapsed, count),
                                )
                            }
                    }
                }
            }
        }
    }

    /** End button in the notification: end the DB session; the observer then stops us. */
    private fun endActiveSession() {
        scope.launch {
            endSessionUseCase()
            dndController.onSessionEnded()
            stopService()
        }
    }

    private fun startForegroundInternal(notification: android.app.Notification) {
        ServiceCompat.startForeground(
            this,
            SessionNotificationHelper.NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
        )
    }

    private fun stopService() {
        tickerJob?.cancel()
        interruptionDetector.stop()
        foregroundAppMonitor.stop()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.deepworktracker.session.ACTION_START"
        const val ACTION_END = "com.deepworktracker.session.ACTION_END"
        const val ACTION_STOP = "com.deepworktracker.session.ACTION_STOP"
    }
}
