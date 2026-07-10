package com.deepworktracker.session.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.deepworktracker.common.time.TimeFormatter
import com.deepworktracker.session.R
import com.deepworktracker.ui.theme.tokens.ComponentColors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [Effect] [DI]
 * Builds the ongoing timer notification shown while a focus session runs.
 * The notification is owned by [FocusSessionService] (foreground service), so
 * [build] returns a [Notification] the service passes to startForeground / notify.
 */
@Singleton
class SessionNotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val CHANNEL_ID = "session_channel"
        const val NOTIFICATION_ID = 1004
    }

    init {
        createChannel()
    }

    private fun createChannel() {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.session_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = context.getString(R.string.session_notification_channel_description)
            setSound(null, null)
            enableVibration(false)
        }
        manager.createNotificationChannel(channel)
    }

    /**
     * [Effect]
     * Input: goal e.g. "Write thesis" (nullable), elapsed e.g. 3m 12s
     * Process: build ongoing silent notification with timer text + End action + tap-to-open
     * Output: a [Notification] ready for startForeground / NotificationManager.notify
     */
    fun build(goal: String?, elapsed: Duration): Notification {
        val title = goal?.takeIf { it.isNotBlank() }
            ?: context.getString(R.string.session_notification_default_title)
        val text = context.getString(
            R.string.session_notification_elapsed,
            TimeFormatter.formatDuration(elapsed),
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText(text)
            .setColor(ComponentColors.focusAccent.toArgb())
            .setOngoing(true)
            .setSilent(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent())
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                context.getString(R.string.session_notification_end_action),
                endActionIntent(),
            )
            .build()
    }

    /**
     * [Effect]
     * Post/refresh the ongoing notification. No-op silently if POST_NOTIFICATIONS is
     * not granted (Android 13+) so tracking degrades gracefully.
     */
    fun notify(notification: Notification) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) return
        runCatching { NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification) }
    }

    /** PendingIntent that re-launches the app's launcher activity (module can't see MainActivity). */
    private fun contentIntent(): PendingIntent? {
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: return null
        return PendingIntent.getActivity(
            context,
            0,
            launch,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /** PendingIntent delivering ACTION_END to the already-running foreground service. */
    private fun endActionIntent(): PendingIntent {
        val intent = Intent(context, FocusSessionService::class.java).apply {
            action = FocusSessionService.ACTION_END
        }
        return PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
