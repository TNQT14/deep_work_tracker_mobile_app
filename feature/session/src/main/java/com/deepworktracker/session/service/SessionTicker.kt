package com.deepworktracker.session.service

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [DI]
 * Single source of the running session's elapsed time. Emits every second the
 * *elapsed-time diff* `now - startTime` rather than counting ticks, so it stays
 * correct across Doze / process restart (roadmap risk mitigation).
 */
@Singleton
class SessionTicker @Inject constructor() {

    /**
     * Input: startTime e.g. session.startTime
     * Process: emit immediately, then re-emit `now - startTime` once per second
     * Output: cold [Flow] of elapsed [Duration]; cancel to stop.
     */
    fun elapsed(startTime: Instant): Flow<Duration> = flow {
        while (true) {
            emit(Clock.System.now() - startTime)
            delay(1000)
        }
    }
}
