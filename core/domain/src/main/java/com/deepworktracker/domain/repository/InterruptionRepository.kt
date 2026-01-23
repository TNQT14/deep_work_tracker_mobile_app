package com.deepworktracker.domain.repository

import com.deepworktracker.domain.model.Interruption
import kotlinx.coroutines.flow.Flow

interface InterruptionRepository {
    fun getInterruptionsBySession(sessionId: String): Flow<List<Interruption>>
    suspend fun getActiveInterruption(sessionId: String): Interruption?
    suspend fun saveInterruption(interruption: Interruption): Result<Unit>
    suspend fun updateInterruption(interruption: Interruption): Result<Unit>
    suspend fun deleteInterruptionsBySession(sessionId: String): Result<Unit>
}
