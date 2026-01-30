package com.deepworktracker.domain.repository

import com.deepworktracker.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface SessionRepository {
    suspend fun getActiveSession(): FocusSession?
    fun observeActiveSession(): Flow<FocusSession?>
    suspend fun getSessionById(id: String): FocusSession?
    fun getSessionsByDate(date: LocalDate): Flow<List<FocusSession>>
    fun getSessionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<FocusSession>>
    suspend fun saveSession(session: FocusSession): Result<Unit>
    suspend fun updateSession(session: FocusSession): Result<Unit>
    suspend fun deleteSession(id: String): Result<Unit>
    suspend fun getRecentGoal(): List<String>
    suspend fun getAllSessions(): List<FocusSession>
}
