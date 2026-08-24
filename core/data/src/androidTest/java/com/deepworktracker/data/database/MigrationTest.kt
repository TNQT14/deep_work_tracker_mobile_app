package com.deepworktracker.data.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        DeepWorkDatabase::class.java,
    )

    @Test
    fun migrate7To8_keepsOldData_andAddsNullDistractionColumn() {
        // Foreign keys are OFF by default in the raw migration db, so we can insert an
        // interruption without a parent focus_sessions row.
        helper.createDatabase(TEST_DB, 7).apply {
            execSQL(
                "INSERT INTO interruptions (id, session_id, start_time, end_time, type, duration) " +
                    "VALUES ('i1', 's1', 1000, 2000, 'APP_SWITCH', 1000)"
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(
            TEST_DB, 8, true, DeepWorkDatabase.MIGRATION_7_8,
        )

        db.query("SELECT type, duration, distraction_package FROM interruptions WHERE id = 'i1'").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("APP_SWITCH", c.getString(0))   // data cũ nguyên vẹn
            assertEquals(1000L, c.getLong(1))
            assertTrue(c.isNull(2))                       // cột mới null
        }
    }

    @Test
    fun migrate8To9_backfillsSittingIdFromSessionId() {
        helper.createDatabase(TEST_DB, 8).apply {
            execSQL(
                """
                INSERT INTO focus_sessions (
                    id, goal, category, start_time, end_time, total_duration, focused_duration,
                    tag, note, date, created_at, updated_at, todo_id,
                    focus_minutes, break_minutes, repeat, alert_mode,
                    actual_focused_minutes, cycles
                ) VALUES (
                    's-old', 'Write thesis', NULL, 1000, 2000, 1000, 1000,
                    NULL, NULL, '2026-08-14', 1000, 1000, 'todo-1',
                    25, 5, 0, 'NOTIFY',
                    0, 0
                )
                """.trimIndent()
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(
            TEST_DB, 9, true, DeepWorkDatabase.MIGRATION_8_9,
        )

        db.query("SELECT sitting_id FROM focus_sessions WHERE id = 's-old'").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("s-old", c.getString(0))
        }
    }

    private companion object {
        const val TEST_DB = "migration-test"
    }
}
