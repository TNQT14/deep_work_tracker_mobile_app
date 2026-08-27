package com.deepworktracker.session.domain.usecase

import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.error.DeepWorkError
import com.deepworktracker.domain.model.AlertMode
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.Interruption
import com.deepworktracker.domain.model.InterruptionType
import com.deepworktracker.domain.repository.InterruptionRepository
import com.deepworktracker.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.minutes

private class FakeSessionRepository(activeSession: FocusSession?) : SessionRepository {
    private val active = MutableStateFlow(activeSession)
    var lastUpdated: FocusSession? = null
    var updateShouldFail = false

    override suspend fun getActiveSession(): FocusSession? = active.value
    override fun observeActiveSession(): Flow<FocusSession?> = active
    override suspend fun getSessionById(id: String): FocusSession? = null
    override fun getSessionsByDate(date: LocalDate): Flow<List<FocusSession>> = flowOf(emptyList())
    override fun getSessionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<FocusSession>> = flowOf(emptyList())
    override suspend fun saveSession(session: FocusSession): kotlin.Result<Unit> = kotlin.Result.success(Unit)
    override suspend fun updateSession(session: FocusSession): kotlin.Result<Unit> {
        lastUpdated = session
        return if (updateShouldFail) kotlin.Result.failure(IllegalStateException()) else kotlin.Result.success(Unit)
    }
    override suspend fun switchActiveSession(
        ended: FocusSession,
        next: FocusSession,
    ): kotlin.Result<Unit> = kotlin.Result.success(Unit)
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

private class FakeInterruptionRepository(
    private val interruptionsBySession: Map<String, List<Interruption>> = emptyMap(),
) : InterruptionRepository {
    val updated = mutableListOf<Interruption>()

    override fun getInterruptionsBySession(sessionId: String): Flow<List<Interruption>> =
        flowOf(interruptionsBySession[sessionId].orEmpty())
    override suspend fun getActiveInterruption(sessionId: String): Interruption? = null
    override suspend fun saveInterruption(interruption: Interruption): kotlin.Result<Unit> = kotlin.Result.success(Unit)
    override suspend fun updateInterruption(interruption: Interruption): kotlin.Result<Unit> {
        updated += interruption
        return kotlin.Result.success(Unit)
    }
    override suspend fun deleteInterruptionsBySession(sessionId: String): kotlin.Result<Unit> = kotlin.Result.success(Unit)
    override suspend fun getInterruptionsBetween(
        from: kotlinx.datetime.Instant,
        to: kotlinx.datetime.Instant,
    ): List<Interruption> = emptyList()
}

class EndSessionUseCaseTest {

    private val t0 = Instant.fromEpochMilliseconds(1_000_000)

    private fun session(id: String = "s1", startTime: Instant = t0) = FocusSession(
        id = id,
        goal = "Write thesis",
        category = null,
        startTime = startTime,
        endTime = null,
        totalDuration = 0,
        focusedDuration = 0,
        tag = null,
        note = null,
        alertMode = AlertMode.NOTIFY,
    )

    private fun interruption(sessionId: String, durationMs: Long, closed: Boolean = true) = Interruption(
        id = "i-$durationMs",
        sessionId = sessionId,
        startTime = t0,
        endTime = if (closed) t0 else null,
        type = InterruptionType.APP_SWITCH,
        duration = durationMs,
    )

    @Test
    fun `subtracts interruption time and writes actualFocusedMinutes`() = runBlocking {
        val active = session(startTime = t0)
        val sessionRepo = FakeSessionRepository(active)
        val interruptionRepo = FakeInterruptionRepository(
            mapOf(active.id to listOf(interruption(active.id, 5.minutes.inWholeMilliseconds))),
        )
        val useCase = EndSessionUseCase(sessionRepo, interruptionRepo)

        val result = useCase()

        assertTrue(result is Result.Success)
        val ended = (result as Result.Success).data
        assertEquals(active.id, ended.id)
        // totalDuration is wall-clock (now - startTime), so assert the relationship rather
        // than an exact value: exactly 5 minutes must have been subtracted for the interruption.
        assertEquals(5.minutes.inWholeMilliseconds, ended.totalDuration - ended.focusedDuration)
        assertEquals((ended.focusedDuration / 60_000L).toInt(), ended.actualFocusedMinutes)
        assertEquals(1, ended.interruptions.size)
        assertEquals(sessionRepo.lastUpdated, ended)
    }

    @Test
    fun `no active session returns NoActiveSession error`() = runBlocking {
        val useCase = EndSessionUseCase(FakeSessionRepository(null), FakeInterruptionRepository())

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(DeepWorkError.NoActiveSession, (result as Result.Error).exception)
    }

    @Test
    fun `repository update failure returns DatabaseError`() = runBlocking {
        val active = session()
        val sessionRepo = FakeSessionRepository(active).apply { updateShouldFail = true }
        val useCase = EndSessionUseCase(sessionRepo, FakeInterruptionRepository())

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(DeepWorkError.DatabaseError, (result as Result.Error).exception)
    }

    @Test
    fun `open interruption is closed and subtracted from focused duration`() = runBlocking {
        val now = Clock.System.now()
        val start = now - 10.minutes
        val interruptionStart = now - 2.minutes
        val active = session(startTime = start)
        val interruptionRepo = FakeInterruptionRepository(
            mapOf(
                active.id to listOf(
                    Interruption(
                        id = "i-open",
                        sessionId = active.id,
                        startTime = interruptionStart,
                        endTime = null,
                        type = InterruptionType.APP_SWITCH,
                        duration = 0,
                    ),
                ),
            ),
        )
        val useCase = EndSessionUseCase(FakeSessionRepository(active), interruptionRepo)

        val result = useCase()

        assertTrue(result is Result.Success)
        val ended = (result as Result.Success).data
        val closed = interruptionRepo.updated.single()
        assertTrue(closed.endTime != null)
        assertEquals(ended.totalDuration - ended.focusedDuration, closed.duration)
        assertTrue(closed.duration >= 90_000L)
    }
}
