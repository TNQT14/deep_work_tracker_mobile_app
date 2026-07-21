package com.deepworktracker.domain.analytics

import com.deepworktracker.domain.model.FocusSession
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime

object FocusHeatmapAggregator {
    const val DAYS = 7
    const val HOURS = 24

    private const val MILLIS_PER_MINUTE = 60_000L

    fun bestFocusHours(sessions: List<FocusSession>, zone: TimeZone, limit: Int = 3): List<Int> {
        val byHour = LongArray(HOURS)
        for (session in sessions) {
            if (session.endTime == null) continue
            val hours = session.endTime.toLocalDateTime(zone).hour
            byHour[hours] += session.focusedDuration
        }

        return byHour.withIndex()
            .filter { it.value > 0L }
            .sortedByDescending { it.value }
            .take(limit).map { it.index }
    }

    fun aggregate(sessions: List<FocusSession>, zone: TimeZone): List<List<Long>> {
        val grid = MutableList(DAYS) { MutableList(HOURS) { 0L } }
        for (session in sessions) {
            if (session.endTime == null) continue
            val dt = session.startTime.toLocalDateTime(zone)
            val row = dt.dayOfWeek.isoDayNumber - 1
            val col = dt.hour
            grid[row][col] += session.focusedDuration
        }
        return grid.map { it.toList() }
    }

}