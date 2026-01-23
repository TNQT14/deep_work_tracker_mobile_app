package com.deepworktracker.domain.repository

import com.deepworktracker.domain.model.DailyStats
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface StatsRepository {
    suspend fun getDailyStats(date: LocalDate): DailyStats?
    fun getStatsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyStats>>
    suspend fun calculateAndSaveDailyStats(date: LocalDate): Result<Unit>
}
