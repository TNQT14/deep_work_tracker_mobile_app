package com.deepworktracker.data.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.deepworktracker.data.database.dao.SessionDao
import com.deepworktracker.data.database.entity.FocusSessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionDaoSittingTotalsTest {

    private lateinit var db: DeepWorkDatabase
    private lateinit var dao: SessionDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            DeepWorkDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.sessionDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun getSittingTotals_sumsSharedSittingAcrossDifferentTodos() = runBlocking {
        dao.insertSession(session(id = "s1", todoId = "todo-a", sittingId = "sit-1", start = 1_000, end = 2_000, duration = 1_000))
        dao.insertSession(session(id = "s2", todoId = "todo-b", sittingId = "sit-1", start = 2_000, end = 4_000, duration = 2_000))
        dao.insertSession(session(id = "s3", todoId = "todo-c", sittingId = "sit-1", start = 4_000, end = 7_000, duration = 3_000))
        dao.insertSession(session(id = "s4", todoId = "todo-d", sittingId = "sit-2", start = 8_000, end = 9_000, duration = 1_000))
        dao.insertSession(
            session(id = "s-active", todoId = "todo-e", sittingId = "sit-1", start = 10_000, end = null, duration = 0)
        )

        val totals = dao.getSittingTotals("2026-08-24", "2026-08-24").first()

        assertEquals(2, totals.size)
        val shared = totals.first { it.sittingId == "sit-1" }
        assertEquals(6_000L, shared.totalMs)
        assertEquals(1_000L, shared.startMs)
        assertEquals(7_000L, shared.endMs)
        assertEquals(1_000L, totals.first { it.sittingId == "sit-2" }.totalMs)
    }

    private fun session(
        id: String,
        todoId: String,
        sittingId: String,
        start: Long,
        end: Long?,
        duration: Long,
    ) = FocusSessionEntity(
        id = id,
        goal = "Focus",
        category = null,
        startTime = start,
        endTime = end,
        totalDuration = duration,
        focusedDuration = duration,
        tag = null,
        note = null,
        date = "2026-08-24",
        todoId = todoId,
        sittingId = sittingId,
    )
}
