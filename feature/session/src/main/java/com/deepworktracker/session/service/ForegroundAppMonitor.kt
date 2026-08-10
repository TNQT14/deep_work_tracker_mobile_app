package com.deepworktracker.session.service

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.deepworktracker.domain.repository.FocusShieldRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import android.os.Process

@Singleton
class ForegroundAppMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val focusShieldRepository: FocusShieldRepository,
    private val detector: InterruptionDetector
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    fun start() {
        if (job != null) return
        job = scope.launch {
            while (isActive) {
                delay(POLL_INTERVAL_MS)
                if (!hasUsageAccess()) continue
                val pkg = lastForegroundPackage() ?: continue
                if(pkg== context.packageName) continue
                val blockList = focusShieldRepository.getConfig().blocklist
                if(pkg in blockList) detector.reportForegroundPackage(pkg)

            }
        }
    }

    fun stop(){
        job?.cancel()
        job = null
    }

    private fun hasUsageAccess(): Boolean {
        val appOps = context.getSystemService(AppOpsManager::class.java)
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun lastForegroundPackage(): String? {
        val usm = context.getSystemService(UsageStatsManager::class.java) ?: return null
        val end = System.currentTimeMillis()
        val events = usm.queryEvents(end - LOOKBACK_MS, end)
        var last: String? = null
        val e = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(e)
            if (e.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) last = e.packageName
        }
        return last
    }

    private companion object {
        const val POLL_INTERVAL_MS = 5_000L
        const val LOOKBACK_MS = 10_000L
    }
}