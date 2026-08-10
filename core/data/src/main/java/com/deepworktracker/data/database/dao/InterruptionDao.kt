package com.deepworktracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.deepworktracker.data.database.entity.InterruptionEntity
import com.deepworktracker.domain.model.Interruption
import kotlinx.coroutines.flow.Flow

@Dao
interface InterruptionDao {
    
    @Query("SELECT * FROM interruptions WHERE session_id = :sessionId ORDER BY start_time ASC")
    fun getInterruptionsBySession(sessionId: String): Flow<List<InterruptionEntity>>
    
    @Query("SELECT * FROM interruptions WHERE session_id = :sessionId AND end_time IS NULL LIMIT 1")
    suspend fun getActiveInterruption(sessionId: String): InterruptionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterruption(interruption: InterruptionEntity)
    
    @Update
    suspend fun updateInterruption(interruption: InterruptionEntity)
    
    @Query("DELETE FROM interruptions WHERE session_id = :sessionId")
    suspend fun deleteInterruptionsBySession(sessionId: String)

    /**
     * [Local]
     * Cross-session query for feature #3 (Insights): counts/lists interruptions in a
     * date range regardless of which session they belong to. start_time has an index
     * (see InterruptionEntity), so this range scan stays cheap.
     * Sample: getInterruptionsBetween(mondayMillis, nextMondayMillis) ->
     *         all InterruptionEntity rows started that week.
     */
    @Query("SELECT * FROM interruptions WHERE start_time >= :fromMillis AND start_time <:toMillis")
    suspend fun getInterruptionsBetween(fromMillis: Long, toMillis:Long): List<InterruptionEntity>
}
