package com.deepworktracker.data.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.deepworktracker.data.database.DeepWorkDatabase
import com.deepworktracker.data.mapper.SessionMapper
import com.deepworktracker.common.datetime.toEpochMilliseconds
import com.deepworktracker.domain.model.FocusSession
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionRepositorySwitchActiveSessionTest {

    private lateinit var db: DeepWorkDatabase
    private lateinit var repository: SessionRepositoryImpl

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            DeepWorkDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = SessionRepositoryImpl(db, db.sessionDao(), SessionMapper())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun switchActiveSession_leavesExactlyOneActiveRow() = runBlocking {
        val ended = session(
            id = "s-old",
            todoId = "todo-a",
            sittingId = "sit-1",
            startMs = 1_000,
            endMs = 2_000,
        )
        val next = session(
            id = "s-new",
            todoId = "todo-b",
            sittingId = "sit-1",
            startMs = 2_000,
            endMs = null,
        )
        repository.saveSession(ended.copy(endTime = null, totalDuration = 0, focusedDuration = 0))

        val result = repository.switchActiveSession(ended, next)

        assertTrue(result.isSuccess)
        val active = repository.getActiveSession()
        assertNotNull(active)
        assertEquals("s-new", active!!.id)
        assertEquals("todo-b", active.todoId)
        assertNull(active.endTime)

        val old = repository.getSessionById("s-old")
        assertNotNull(old)
        assertEquals(2_000L, old!!.endTime?.toEpochMilliseconds())
    }

    private fun session(
        id: String,
        todoId: String,
        sittingId: String,
        startMs: Long,
        endMs: Long?,
    ) = FocusSession(
        id = id,
        goal = "Focus",
        category = null,
        startTime = Instant.fromEpochMilliseconds(startMs),
        endTime = endMs?.let { Instant.fromEpochMilliseconds(it) },
        totalDuration = if (endMs != null) endMs - startMs else 0L,
        focusedDuration = if (endMs != null) endMs - startMs else 0L,
        tag = null,
        note = null,
        todoId = todoId,
        sittingId = sittingId,
    )
}
