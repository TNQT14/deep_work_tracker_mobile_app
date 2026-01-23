package com.deepworktracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.deepworktracker.data.database.dao.InterruptionDao
import com.deepworktracker.data.database.dao.SessionDao
import com.deepworktracker.data.database.dao.StatsDao
import com.deepworktracker.data.database.dao.InsightDao
import com.deepworktracker.data.database.entity.DailyStatsEntity
import com.deepworktracker.data.database.entity.FocusSessionEntity
import com.deepworktracker.data.database.entity.InterruptionEntity
import com.deepworktracker.data.database.entity.InsightEntity

@Database(
    entities = [
        FocusSessionEntity::class,
        InterruptionEntity::class,
        DailyStatsEntity::class,
        InsightEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DeepWorkDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun interruptionDao(): InterruptionDao
    abstract fun statsDao(): StatsDao
    abstract fun insightDao(): InsightDao
    
    companion object {
        const val DATABASE_NAME = "deep_work_db"
    }
}
