package com.deepworktracker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deepworktracker.common.result.Result as DwResult
import com.deepworktracker.dashboard.domain.usecase.GenerateInsightsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * [DI] Background trigger for feature #3 (Insights), scheduled periodic every 24h from
 * DeepWorkApplication.scheduleInsightsWork(). WorkManager instantiates this worker
 * itself — not Hilt — so `appContext`/`params` are @Assisted (supplied at runtime by
 * WorkManager) while `generateInsights` is @Inject'd normally via HiltWorkerFactory
 * (registered as Application's Configuration.Provider).
 *
 * IMPORTANT: never reach into UI/ViewModel state from here. When this runs, the app
 * process may not even exist (WorkManager can wake the app from a dead process) — the
 * only valid output of doWork() is writing to Room; the UI picks new rows up later via
 * InsightRepository.getRecentInsights()'s Flow, whenever the Activity/ViewModel exist.
 */
@HiltWorker
class GenerateInsightsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val generateInsights: GenerateInsightsUseCase,
) : CoroutineWorker(appContext, params) {

    /**
     * Input: none (WorkManager calls this with zero arguments)
     * Process: runs the full insight pipeline via GenerateInsightsUseCase — build
     *          StatsWindow, evaluate all 4 rules, persist any that fired.
     * Output: Result.success() on DwResult.Success (even if 0 insights were produced —
     *         "nothing to say" is not a failure); Result.retry() on DwResult.Error,
     *         which lets WorkManager reschedule with its default backoff policy.
     */
    override suspend fun doWork(): Result {
        return when (generateInsights()) {
            is DwResult.Success -> Result.success()
            is DwResult.Error -> Result.retry()
        }
    }

    companion object {
        const val UNIQUE_NAME = "generate_insights_periodic"
    }
}
