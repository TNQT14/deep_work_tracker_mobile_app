package com.deepworktracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.deepworktracker.data.database.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    
    @Query("SELECT * FROM focus_sessions WHERE id = :id")
    suspend fun getSessionById(id: String): FocusSessionEntity?
    
    @Query("SELECT * FROM focus_sessions WHERE end_time IS NULL LIMIT 1")
    suspend fun getActiveSession(): FocusSessionEntity?
    
    @Query("SELECT * FROM focus_sessions WHERE end_time IS NULL LIMIT 1")
    fun observeActiveSession(): Flow<FocusSessionEntity?>
    
    @Query("SELECT * FROM focus_sessions WHERE date = :date ORDER BY start_time DESC")
    fun getSessionsByDate(date: String): Flow<List<FocusSessionEntity>>
    
    @Query("SELECT * FROM focus_sessions WHERE date BETWEEN :startDate AND :endDate ORDER BY start_time DESC")
    fun getSessionsByDateRange(startDate: String, endDate: String): Flow<List<FocusSessionEntity>>
    
    @Query("SELECT * FROM focus_sessions ORDER BY start_time DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 50): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions ORDER BY start_time DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity)
    
    @Update
    suspend fun updateSession(session: FocusSessionEntity)
    
    @Delete
    suspend fun deleteSession(session: FocusSessionEntity)
    
    @Query("DELETE FROM focus_sessions WHERE date < :beforeDate")
    suspend fun deleteSessionsBefore(beforeDate: String)
}
