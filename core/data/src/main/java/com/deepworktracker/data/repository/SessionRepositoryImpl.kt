package com.deepworktracker.data.repository

import com.deepworktracker.data.database.dao.SessionDao
import com.deepworktracker.data.database.entity.FocusSessionEntity
import com.deepworktracker.data.mapper.SessionMapper
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val mapper: SessionMapper
) : SessionRepository {

    override suspend fun getActiveSession(): FocusSession? {
        return sessionDao.getActiveSession()?.let { mapper.toDomain(it) }
    }

    override fun observeActiveSession(): Flow<FocusSession?> {
        return sessionDao.observeActiveSession().map { it?.let { mapper.toDomain(it) } }
    }

    override suspend fun getSessionById(id: String): FocusSession? {
        return sessionDao.getSessionById(id)?.let { mapper.toDomain(it) }
    }

    override fun getSessionsByDate(date: LocalDate): Flow<List<FocusSession>> {
        val dateString = date.toString()
        return sessionDao.getSessionsByDate(dateString)
            .map { entities -> entities.map { mapper.toDomain(it) } }
    }

    override fun getSessionsByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<FocusSession>> {
        return sessionDao.getSessionsByDateRange(startDate.toString(), endDate.toString())
            .map { entities -> entities.map { mapper.toDomain(it) } }
    }

    override suspend fun saveSession(session: FocusSession): Result<Unit> {
        return try {
            val entity = mapper.toEntity(session)
            sessionDao.insertSession(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSession(session: FocusSession): Result<Unit> {
        return try {
            val entity = mapper.toEntity(session)
            sessionDao.updateSession(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSession(id: String): Result<Unit> {
        return try {
            val session = getSessionById(id)
            if (session != null) {
                val entity = mapper.toEntity(session)
                sessionDao.deleteSession(entity)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecentGoal(): List<String> {
        return try {
            val resentSessions = sessionDao.getRecentSessions().first()
            val uniqueGoals = resentSessions.map {
                it.goal
            }.distinct().take(5)
            uniqueGoals
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getAllSessions(): List<FocusSession> {
        return try {
            val allSessionEntity = sessionDao.getAllSessions()
            val allSession = allSessionEntity.first().map { mapper.toDomain(it) }
            allSession

        } catch (e: Exception) {
            emptyList()
        }
    }
}
