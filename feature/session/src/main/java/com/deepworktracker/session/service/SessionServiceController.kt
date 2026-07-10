package com.deepworktracker.session.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [DI]
 * Thin launcher so the ViewModel can start/stop [FocusSessionService] without touching
 * Context or Intent directly. Injected into SessionViewModel.
 */
@Singleton
class SessionServiceController @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    /** Start the foreground service for a newly started (or already active) session. */
    fun start() {
        val intent = Intent(context, FocusSessionService::class.java).apply {
            action = FocusSessionService.ACTION_START
        }
        ContextCompat.startForegroundService(context, intent)
    }

    /** Stop the service after the session has already been ended in the DB. */
    fun stop() {
        val intent = Intent(context, FocusSessionService::class.java).apply {
            action = FocusSessionService.ACTION_STOP
        }
        ContextCompat.startForegroundService(context, intent)
    }
}
