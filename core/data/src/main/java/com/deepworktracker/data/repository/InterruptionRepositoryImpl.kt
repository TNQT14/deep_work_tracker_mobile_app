package com.deepworktracker.data.repository

import com.deepworktracker.data.database.dao.InterruptionDao
import com.deepworktracker.data.mapper.InterruptionMapper
import com.deepworktracker.domain.model.Interruption
import com.deepworktracker.domain.repository.InterruptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import javax.inject.Inject

class InterruptionRepositoryImpl @Inject constructor(
    private val interruptionDao: InterruptionDao,
    private val mapper: InterruptionMapper
) : InterruptionRepository {

    override fun getInterruptionsBySession(sessionId: String): Flow<List<Interruption>> {
        return interruptionDao.getInterruptionsBySession(sessionId)
            .map { entities -> entities.map { mapper.toDomain(it) } }
    }

    override suspend fun getActiveInterruption(sessionId: String): Interruption? {
        return interruptionDao.getActiveInterruption(sessionId)?.let { mapper.toDomain(it) }
    }

    override suspend fun saveInterruption(interruption: Interruption): Result<Unit> {
        return try {
            val entity = mapper.toEntity(interruption)
            interruptionDao.insertInterruption(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateInterruption(interruption: Interruption): Result<Unit> {
        return try {
            val entity = mapper.toEntity(interruption)
            interruptionDao.updateInterruption(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteInterruptionsBySession(sessionId: String): Result<Unit> {
        return try {
            interruptionDao.deleteInterruptionsBySession(sessionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * [Repository]
     * Input: from/to — kotlinx.datetime.Instant (timezone-agnostic UTC instants)
     * Process: converts to epoch millis, delegates to InterruptionDao, maps each
     *          InterruptionEntity back to the domain Interruption model.
     * Output: List<Interruption> — flat list across sessions; callers that need it
     *         grouped by day (e.g. DistractionPatternRule) do that grouping themselves
     *         using kotlinx.datetime, not SQL, to stay timezone-correct.
     */
    override suspend fun getInterruptionsBetween(from: Instant, to: Instant): List<Interruption> {
        return interruptionDao.getInterruptionsBetween(
            from.toEpochMilliseconds(),
            to.toEpochMilliseconds()
        ).map { mapper.toDomain(it) }
    }
}
