package com.deepworktracker.session.service

import android.app.NotificationManager
import android.content.Context
import com.deepworktracker.domain.repository.FocusShieldRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DndController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val focusShieldRepository: FocusShieldRepository,
) {
    private val nm: NotificationManager
        get() = context.getSystemService(NotificationManager::class.java)

    fun hasPolicyAccess(): Boolean = nm.isNotificationPolicyAccessGranted

    /** Called when a focus session starts (Bước 3 sẽ gọi từ service). */
    suspend fun onSessionStarted() {
        val config = focusShieldRepository.getConfig()
        val decision = DndPolicy.onStart(
            dndEnabled = config.dndEnabled,
            hasPolicyAccess = hasPolicyAccess(),
            alreadyHolding = focusShieldRepository.getPreviousDndFilter() != null,
            applyFilter = APPLIED_FILTER,
        )
        if (decision is DndPolicy.StartDecision.Enable) {
            focusShieldRepository.setPreviousDndFilter(nm.currentInterruptionFilter)
            runCatching { nm.setInterruptionFilter(decision.filterToApply) }
        }
    }

    /** Called when a focus session ends / service stops without a session. */
    suspend fun onSessionEnded() {
        val hasAccess = hasPolicyAccess()
        val decision = DndPolicy.onStop(
            previousFilter = focusShieldRepository.getPreviousDndFilter(),
            currentSystemFilter = if (hasAccess) nm.currentInterruptionFilter else APPLIED_FILTER,
            appliedFilter = APPLIED_FILTER,
        )
        when (decision) {
            is DndPolicy.StopDecision.Restore -> {
                if (hasAccess) runCatching { nm.setInterruptionFilter(decision.filter) }
                focusShieldRepository.setPreviousDndFilter(null)
            }
            DndPolicy.StopDecision.ClearOnly -> focusShieldRepository.setPreviousDndFilter(null)
            DndPolicy.StopDecision.NoOp -> Unit
        }
    }

    companion object {
        const val APPLIED_FILTER = NotificationManager.INTERRUPTION_FILTER_PRIORITY
    }
}