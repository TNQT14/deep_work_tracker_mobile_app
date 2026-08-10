package com.deepworktracker.domain.repository

import com.deepworktracker.domain.model.Interruption
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface InterruptionRepository {
    fun getInterruptionsBySession(sessionId: String): Flow<List<Interruption>>
    suspend fun getActiveInterruption(sessionId: String): Interruption?
    suspend fun saveInterruption(interruption: Interruption): Result<Unit>
    suspend fun updateInterruption(interruption: Interruption): Result<Unit>
    suspend fun deleteInterruptionsBySession(sessionId: String): Result<Unit>

    /**
     * [Repository]
     * Input: from/to (Instant, half-open range [from, to))
     * Process: delegates to Room, filtering on start_time.
     * Output: List<Interruption> across ALL sessions in range — unlike
     *         getInterruptionsBySession, this is NOT scoped to one session. Added for
     *         feature #3 (Insights): GenerateInsightsUseCase needs interruption counts
     *         aggregated across a whole analytics period, and FocusSession.interruptions
     *         is always empty (SessionMapper never populates it), so this is the only
     *         way to get that data.
     */
    suspend fun getInterruptionsBetween(from: Instant, to: Instant): List<Interruption>
}
