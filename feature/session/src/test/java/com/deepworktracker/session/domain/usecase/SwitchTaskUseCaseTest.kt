package com.deepworktracker.session.domain.usecase

import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.error.DeepWorkError
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.Interruption
import com.deepworktracker.domain.repository.InterruptionRepository
import com.deepworktracker.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

private class RecordingSessionRepository(activeSession: FocusSession?) : SessionRepository {
    private val active = MutableStateFlow(activeSession)
    val saved = mutableListOf<FocusSession>()
    val updated = mutableListOf<FocusSession>()

    override suspend fun getActiveSession(): FocusSession? = active.value
    override fun observeActiveSession(): Flow<FocusSession?> = active
    override suspend fun getSessionById(id: String): FocusSession? =
        listOfNotNull(active.value).plus(saved).plus(updated).lastOrNull { it.id == id }

    override fun getSessionsByDate(date: LocalDate): Flow<List<FocusSession>> = flowOf(emptyList())
    override fun getSessionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<FocusSession>> =
        flowOf(emptyList())

    override suspend fun saveSession(session: FocusSession): kotlin.Result<Unit> {
        saved += session
        active.value = session
        return kotlin.Result.success(Unit)
    }

    override suspend fun updateSession(session: FocusSession): kotlin.Result<Unit> {
        updated += session
        if (session.endTime != null && active.value?.id == session.id) {
            active.value = null
        } else {
            active.value = session
        }
        return kotlin.Result.success(Unit)
    }

    override suspend fun deleteSession(id: String): kotlin.Result<Unit> = kotlin.Result.success(Unit)
    override suspend fun getRecentGoal(): List<String> = emptyList()
    override suspend fun getRecentCategories(limit: Int): List<String> = emptyList()
    override suspend fun getRecentTags(limit: Int): List<String> = emptyList()
    override suspend fun getAllSessions(): List<FocusSession> = emptyList()
    override fun getSessionsByTodoId(todoId: String): Flow<List<FocusSession>> = flowOf(emptyList())
    override fun getDailyFocusedMillis(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<Map<LocalDate, Long>> = flowOf(emptyMap())
}

private class EmptyInterruptionRepository : InterruptionRepository {
    override fun getInterruptionsBySession(sessionId: String): Flow<List<Interruption>> = flowOf(emptyList())
    override suspend fun getActiveInterruption(sessionId: String): Interruption? = null
    override suspend fun saveInterruption(interruption: Interruption): kotlin.Result<Unit> = kotlin.Result.success(Unit)
    override suspend fun updateInterruption(interruption: Interruption): kotlin.Result<Unit> = kotlin.Result.success(Unit)
    override suspend fun deleteInterruptionsBySession(sessionId: String): kotlin.Result<Unit> = kotlin.Result.success(Unit)
    override suspend fun getInterruptionsBetween(from: Instant, to: Instant): List<Interruption> = emptyList()
}

class SwitchTaskUseCaseTest {

    private val t0 = Instant.fromEpochMilliseconds(1_000_000)

    private fun session(
        id: String = "s1",
        sittingId: String? = null,
        todoId: String? = "todo-old",
    ) = FocusSession(
        id = id,
        goal = "Write intro",
        category = "Thesis",
        startTime = t0,
        endTime = null,
        totalDuration = 0,
        focusedDuration = 0,
        tag = null,
        note = null,
        todoId = todoId,
        sittingId = sittingId,
    )

    @Test
    fun `ends old row and starts new row with shared sittingId`() = runBlocking {
        val old = session(sittingId = "sit-1")
        val repo = RecordingSessionRepository(old)
        val useCase = SwitchTaskUseCase(repo, EndSessionUseCase(repo, EmptyInterruptionRepository()))

        val result = useCase(newTodoId = "todo-new", newGoal = "Write methods")

        assertTrue(result is Result.Success)
        val next = (result as Result.Success).data
        val ended = repo.updated.single()
        assertEquals("sit-1", ended.sittingId)
        assertEquals("sit-1", next.sittingId)
        assertEquals("todo-old", ended.todoId)
        assertEquals("todo-new", next.todoId)
        assertEquals("Write methods", next.goal)
        assertNotNull(ended.endTime)
        assertEquals(ended.endTime, next.startTime)
        assertEquals(null, next.endTime)
    }

    @Test
    fun `uses old session id when sittingId is null`() = runBlocking {
        val old = session(id = "s-first", sittingId = null)
        val repo = RecordingSessionRepository(old)
        val useCase = SwitchTaskUseCase(repo, EndSessionUseCase(repo, EmptyInterruptionRepository()))

        val result = useCase(newTodoId = "todo-new", newGoal = "Next task")

        assertTrue(result is Result.Success)
        assertEquals("s-first", (result as Result.Success).data.sittingId)
        assertEquals("s-first", repo.updated.single().id)
    }

    @Test
    fun `no active session returns NoActiveSession`() = runBlocking {
        val repo = RecordingSessionRepository(null)
        val useCase = SwitchTaskUseCase(repo, EndSessionUseCase(repo, EmptyInterruptionRepository()))

        val result = useCase(newTodoId = "todo-new", newGoal = "Next")

        assertTrue(result is Result.Error)
        assertEquals(DeepWorkError.NoActiveSession, (result as Result.Error).exception)
        assertTrue(repo.saved.isEmpty())
    }
}
