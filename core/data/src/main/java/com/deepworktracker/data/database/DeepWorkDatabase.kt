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
import com.deepworktracker.data.database.dao.TodoDao
import com.deepworktracker.data.database.entity.DailyStatsEntity
import com.deepworktracker.data.database.entity.FocusSessionEntity
import com.deepworktracker.data.database.entity.InterruptionEntity
import com.deepworktracker.data.database.entity.InsightEntity
import com.deepworktracker.data.database.entity.CategoryRuleEntity
import com.deepworktracker.data.database.entity.TodoEntity

@Database(
    entities = [
        FocusSessionEntity::class,
        InterruptionEntity::class,
        DailyStatsEntity::class,
        InsightEntity::class,
        CategoryRuleEntity::class,
        TodoEntity::class
    ],
    version = 9,
    exportSchema = true
)
abstract class DeepWorkDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun interruptionDao(): InterruptionDao
    abstract fun statsDao(): StatsDao
    abstract fun insightDao(): InsightDao
    abstract fun categoryRuleDao(): CategoryRuleDao
    abstract fun todoDao(): TodoDao
    
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

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS todos (
                        id TEXT NOT NULL PRIMARY KEY,
                        category TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        status TEXT NOT NULL,
                        priority INTEGER NOT NULL,
                        due_at INTEGER,
                        completed_at INTEGER,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_category ON todos(category)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_status ON todos(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_created_at ON todos(created_at)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_due_at ON todos(due_at)")
            }
        }

        /**
         * Repairs [MIGRATION_2_3] when it created `status` as INTEGER, nullable `description`,
         * or `priority` with DEFAULT — Room expects TEXT status and NOT NULL description.
         */
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS todos_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        category TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        status TEXT NOT NULL,
                        priority INTEGER NOT NULL,
                        due_at INTEGER,
                        completed_at INTEGER,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO todos_new (
                        id, category, title, description, status, priority,
                        due_at, completed_at, created_at, updated_at
                    )
                    SELECT
                        id,
                        category,
                        title,
                        COALESCE(description, ''),
                        CASE
                            WHEN typeof(status) = 'integer' THEN
                                CASE status
                                    WHEN 0 THEN 'TODO'
                                    WHEN 1 THEN 'IN_PROGRESS'
                                    WHEN 2 THEN 'PAUSED'
                                    WHEN 3 THEN 'DONE'
                                    ELSE 'TODO'
                                END
                            ELSE CAST(status AS TEXT)
                        END,
                        priority,
                        due_at,
                        completed_at,
                        created_at,
                        updated_at
                    FROM todos
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE todos")
                db.execSQL("ALTER TABLE todos_new RENAME TO todos")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_category ON todos(category)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_status ON todos(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_created_at ON todos(created_at)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_due_at ON todos(due_at)")
            }
        }

        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE todos ADD COLUMN estimated_minutes INTEGER")
            }
        }

        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE todos ADD COLUMN remaining_minutes INTEGER")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN todo_id TEXT")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN focus_minutes INTEGER NOT NULL DEFAULT 25")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN break_minutes INTEGER NOT NULL DEFAULT 5")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN repeat INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN alert_mode TEXT NOT NULL DEFAULT 'NOTIFY'")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN actual_focused_minutes INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN cycles INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS todos_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        goal TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        status TEXT NOT NULL,
                        priority INTEGER NOT NULL,
                        due_at INTEGER,
                        completed_at INTEGER,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO todos_new (
                        id, goal, title, description, status, priority,
                        due_at, completed_at, created_at, updated_at
                    )
                    SELECT
                        id, category, title, description, status, priority,
                        due_at, completed_at, created_at, updated_at
                    FROM todos
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE todos")
                db.execSQL("ALTER TABLE todos_new RENAME TO todos")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_goal ON todos(goal)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_status ON todos(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_created_at ON todos(created_at)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_due_at ON todos(due_at)")
            }
        }

        val MIGRATION_7_8: Migration = object : Migration(7,8){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE interruptions ADD COLUMN distraction_package TEXT")
            }
        }

        val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE focus_sessions ADD COLUMN sitting_id TEXT")
                db.execSQL("UPDATE focus_sessions SET sitting_id = id WHERE sitting_id IS NULL")
            }
        }
    }
}
