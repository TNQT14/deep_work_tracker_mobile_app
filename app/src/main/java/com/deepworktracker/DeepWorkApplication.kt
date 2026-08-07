package com.deepworktracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.deepworktracker.worker.GenerateInsightsWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * [DI] Composition root. Implements Configuration.Provider so WorkManager initializes
 * on-demand with our HiltWorkerFactory instead of its default startup provider (which
 * is explicitly disabled in AndroidManifest.xml) — this is what lets
 * GenerateInsightsWorker receive @Inject'd dependencies via Hilt.
 */
@HiltAndroidApp
class DeepWorkApplication : Application(), Configuration.Provider {

    /**
     * [DI]
     * Type: HiltWorkerFactory
     * Bridges Hilt's DI graph into WorkManager: whenever WorkManager needs to
     * instantiate a @HiltWorker (e.g. GenerateInsightsWorker), it asks this factory,
     * which resolves the worker's @Inject dependencies from the Hilt graph.
     */
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // On-demand WorkManager init (default startup initializer is removed in Manifest).
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        scheduleInsightsWork()
    }

    /**
     * [DI]
     * Input: none
     * Process: builds a Constraints requiring no charging (rule-based insight
     *          generation is pure computation + local DB reads/writes, no network,
     *          no heavy CPU — fine to run under any battery condition), then enqueues
     *          a 24h PeriodicWorkRequest for GenerateInsightsWorker.
     *          ExistingPeriodicWorkPolicy.KEEP means: if this unique work is already
     *          scheduled from a previous app launch, do nothing — this avoids resetting
     *          the 24h countdown every time the app is opened.
     * Output: WorkManager persists the request to its own database; survives process
     *         death and device reboot (WorkManager re-registers itself on boot).
     */
    private fun scheduleInsightsWork() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .build()

        val request = PeriodicWorkRequestBuilder<GenerateInsightsWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            GenerateInsightsWorker.UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
