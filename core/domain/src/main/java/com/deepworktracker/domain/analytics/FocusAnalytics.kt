package com.deepworktracker.domain.analytics

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.LocalDate

data class FocusAnalytics(
    val period: AnalyticsPeriod,
    val focusScore: Float,
    val totalFocusedMinutes: Long,
    val sessionCount: Int,
    val avgSessionMinutes: Long,
    val bestFocusHour: Int?,
    val dailyTrendMinutes: List<Long>,
    val heatmap: List<List<Long>>,
    val bestFocusHours: List<Int>,
) {
    companion object {
        fun empty(period: AnalyticsPeriod) = FocusAnalytics(
            period = period,
            focusScore = 0f,
            totalFocusedMinutes = 0L,
            sessionCount = 0,
            avgSessionMinutes = 0L,
            bestFocusHour = null,
            dailyTrendMinutes = emptyList(),
            heatmap = List(7) { List(24) { 0L } },
            bestFocusHours = emptyList(),
        )
    }
}

enum class AnalyticsPeriod {
    DAY, WEEK, MONTH;

    fun dateRange(today: LocalDate): Pair<LocalDate, LocalDate> = when (this) {
        DAY -> today to today
        WEEK -> today.minus(6, DateTimeUnit.DAY) to today
        MONTH -> today.minus(29, DateTimeUnit.DAY) to today
    }
}