package com.deepworktracker.data.database

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.deepworktracker.data.database.dao.InterruptionDao
import com.deepworktracker.data.database.dao.SessionDao
import com.deepworktracker.data.database.dao.StatsDao
import com.deepworktracker.data.database.dao.InsightDao
import com.deepworktracker.data.database.dao.CategoryRuleDao
import com.deepworktracker.data.database.entity.DailyStatsEntity
import com.deepworktracker.data.database.entity.FocusSessionEntity
import com.deepworktracker.data.database.entity.InterruptionEntity
import com.deepworktracker.data.database.entity.InsightEntity
import com.deepworktracker.data.database.entity.CategoryRuleEntity

@Database(
    entities = [
        FocusSessionEntity::class,
        InterruptionEntity::class,
        DailyStatsEntity::class,
        InsightEntity::class,
        CategoryRuleEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DeepWorkDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun interruptionDao(): InterruptionDao
    abstract fun statsDao(): StatsDao
    abstract fun insightDao(): InsightDao
    abstract fun categoryRuleDao(): CategoryRuleDao
    
    companion object {
        const val DATABASE_NAME = "deep_work_db"

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN category TEXT")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS category_rules (
                        id TEXT NOT NULL PRIMARY KEY,
                        keyword TEXT,
                        start_hour INTEGER,
                        end_hour INTEGER,
                        category TEXT NOT NULL,
                        tag TEXT,
                        priority INTEGER NOT NULL,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL("CREATE INDEX IF NOT EXISTS index_category_rules_priority ON category_rules(priority)")
            }
        }
    }
}
