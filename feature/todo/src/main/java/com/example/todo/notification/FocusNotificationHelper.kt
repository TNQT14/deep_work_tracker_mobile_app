package com.example.todo.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.deepworktracker.ui.theme.tokens.ComponentColors
import com.example.todo.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [Effect] [DI]
 * Notifications for the focus feature: an ongoing progress notification (focus/break)
 * plus a one-shot completion alert. Injected into FocusViewModel.
 * No action buttons — the timer lives in-memory in the ViewModel (no foreground service),
 * so notifications are content + tap-to-reopen only.
 */
@Singleton
class FocusNotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private const val CHANNEL_ID = "focus_channel"
        private const val COMPLETE_CHANNEL_ID = "focus_complete_channel"
        private const val NOTIFICATION_ID = 1002
        private const val COMPLETE_NOTIFICATION_ID = 1003
    }

    init {
        createChannels()
    }

    private fun createChannels() {
        val manager = context.getSystemService(NotificationManager::class.java)
        val ongoing = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.countdown_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = context.getString(R.string.focus_notification_channel_description)
            setSound(null, null)
            enableVibration(false)
        }
        val complete = NotificationChannel(
            COMPLETE_CHANNEL_ID,
            context.getString(R.string.focus_complete_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        manager.createNotificationChannel(ongoing)
        manager.createNotificationChannel(complete)
    }

    /**
     * [Effect]
     * PendingIntent that re-launches the app's launcher activity (MainActivity).
     * feature:todo can't reference MainActivity directly, so resolve via package name.
     */
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

    fun showFocus(todoTitle: String?, remainingSeconds: Int, cycles: Int) {
        val title = todoTitle?.takeIf { it.isNotBlank() }
            ?: context.getString(R.string.focus_notification_default_title)
        val cycleText = if (cycles > 0) {
            context.getString(R.string.focus_notification_cycle_suffix, cycles)
        } else {
            ""
        }
        val text = context.getString(
            R.string.countdown_notification_remaining,
            formatTime(remainingSeconds),
        ) + cycleText

        notify(title, text, ComponentColors.focusAccent.toArgb())
    }

    /**
     * [Effect]
     * Input: todoTitle e.g. "Write report", remainingSeconds e.g. 240
     * Process: build break-phase title/text → notify() (same NOTIFICATION_ID as focus)
     * Output: ongoing silent notification while break timer runs
     */
    fun showBreak(todoTitle: String?, remainingSeconds: Int) {
        val title = todoTitle?.takeIf { it.isNotBlank() }
            ?: context.getString(R.string.focus_break_phase_label)
        val text = context.getString(
            R.string.focus_notification_break_remaining,
            formatTime(remainingSeconds),
        )
        notify(title, text, ComponentColors.focusBreak.toArgb())
    }

    /**
     * [Effect]
     * Input: focusedMinutes e.g. 48, cycles e.g. 2
     * Process: cancel ongoing notification → post a non-ongoing, auto-cancel completion alert
     * Output: heads-up "session complete" notification; tap reopens the app
     */
    fun showCompleted(todoTitle: String?, focusedMinutes: Int, cycles: Int) {
        cancel()
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return

        val title = todoTitle?.takeIf { it.isNotBlank() }
            ?: context.getString(R.string.focus_notification_completed_title)
        val text = context.getString(
            R.string.focus_notification_completed_text,
            focusedMinutes,
            cycles,
        )
        val notification = NotificationCompat.Builder(context, COMPLETE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText(text)
            .setColor(ComponentColors.focusBreak.toArgb())
            .setAutoCancel(true)
            .setContentIntent(contentIntent())
            .build()

        runCatching {
            NotificationManagerCompat.from(context).notify(COMPLETE_NOTIFICATION_ID, notification)
        }
    }

    fun cancel() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    private fun notify(title: String, text: String, color: Int) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText(text)
            .setColor(color)
            .setOngoing(true)
            .setSilent(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent())
            .build()

        runCatching { NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification) }
    }

    private fun formatTime(totalSeconds: Int): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return String.format("%02d:%02d", m, s)
    }
}
