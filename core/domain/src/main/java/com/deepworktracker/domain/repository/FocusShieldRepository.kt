package com.deepworktracker.domain.repository

import com.deepworktracker.domain.model.FocusShieldConfig
import kotlinx.coroutines.flow.Flow

/**
 * Persistence for Focus Shield (roadmap #4). Holds both the user-chosen config and a small
 * amount of runtime state the DND controller needs to restore the system's Do Not Disturb
 * filter safely across process death (see [getPreviousDndFilter]).
 */
interface FocusShieldRepository {
    /** User-chosen config, reactive for the settings UI. */
    fun observeConfig(): Flow<FocusShieldConfig>

    suspend fun getConfig(): FocusShieldConfig

    suspend fun setDndEnabled(enabled: Boolean): Result<Unit>

    suspend fun setBlocklist(packages: Set<String>): Result<Unit>

    /**
     * The system DND interruption filter captured right before the app turned DND on, using the
     * `NotificationManager.INTERRUPTION_FILTER_*` constants. `null` means the app is NOT currently
     * holding DND — it doubles as the "shield active" flag so no second boolean is needed.
     */
    suspend fun getPreviousDndFilter(): Int?

    suspend fun setPreviousDndFilter(filter: Int?): Result<Unit>
}
