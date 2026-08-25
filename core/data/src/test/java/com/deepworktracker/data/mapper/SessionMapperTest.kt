package com.deepworktracker.data.mapper

import com.deepworktracker.domain.model.FocusSession
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionMapperTest {

    private val mapper = SessionMapper()

    private fun session(sittingId: String? = null) = FocusSession(
        id = "s1",
        goal = "Write thesis",
        category = null,
        startTime = Instant.fromEpochMilliseconds(1_000L),
        endTime = Instant.fromEpochMilliseconds(2_000L),
        totalDuration = 1_000L,
        focusedDuration = 1_000L,
        tag = null,
        note = null,
        sittingId = sittingId,
    )

    @Test
    fun `toEntity uses session id when sittingId is null`() {
        val entity = mapper.toEntity(session(sittingId = null))
        assertEquals("s1", entity.sittingId)
    }

    @Test
    fun `toEntity keeps explicit sittingId`() {
        val entity = mapper.toEntity(session(sittingId = "sit-shared"))
        assertEquals("sit-shared", entity.sittingId)
    }

    @Test
    fun `toDomain maps sittingId`() {
        val entity = mapper.toEntity(session(sittingId = "sit-shared"))
        val domain = mapper.toDomain(entity)
        assertEquals("sit-shared", domain.sittingId)
    }
}
