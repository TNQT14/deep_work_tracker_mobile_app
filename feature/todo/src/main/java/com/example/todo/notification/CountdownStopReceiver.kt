package com.example.todo.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todo.presentation.countdown.CountdownStateStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CountdownStopReceiver : BroadcastReceiver() {

    @Inject
    lateinit var stateStore: CountdownStateStore

    @Inject
    lateinit var notificationHelper: CountdownNotificationHelper

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != CountdownNotificationHelper.ACTION_STOP) return
        scope.launch {
            stateStore.clear()
            notificationHelper.cancel()
        }
    }
}
