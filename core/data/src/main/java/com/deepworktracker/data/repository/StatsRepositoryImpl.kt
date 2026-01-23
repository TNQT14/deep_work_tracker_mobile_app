package com.deepworktracker.data.repository

import com.deepworktracker.data.database.dao.SessionDao
import com.deepworktracker.data.database.dao.StatsDao
import com.deepworktracker.data.mapper.SessionMapper
import com.deepworktracker.data.mapper.StatsMapper
import com.deepworktracker.domain.model.DailyStats
import com.deepworktracker.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class StatsRepositoryImpl @Inject constructor(
    private val statsDao: StatsDao,
    private val sessionDao: SessionDao,
    private val statsMapper: StatsMapper,
    private val sessionMapper: SessionMapper
) : StatsRepository {
    
    override suspend fun getDailyStats(date: LocalDate): DailyStats? {
        return statsDao.getDailyStats(date.toString())?.let { statsMapper.toDomain(it) }
    }
    
    override fun getStatsByDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<DailyStats>> {
        return statsDao.getStatsByDateRange(startDate.toString(), endDate.toString())
            .map { entities -> entities.map { statsMapper.toDomain(it) } }
    }
    
    override suspend fun calculateAndSaveDailyStats(date: LocalDate): Result<Unit> {
        return try {
            val dateString = date.toString()
            val sessions = sessionDao.getSessionsByDate(dateString)
                .map { it.map { sessionMapper.toDomain(it) } }
            
            // Calculate stats from sessions
            // This is a simplified version - in production, you'd want more sophisticated calculation
            val stats = DailyStats(
                date = date,
                totalFocusTime = 0L,
                sessionCount = 0,
                interruptionCount = 0,
                averageSessionDuration = 0L,
                bestFocusHour = null
            )
            
            val entity = statsMapper.toEntity(stats)
            statsDao.insertStats(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
