package com.deepworktracker.dashboard.presentation.goal_detail

data class GoalMetrics(
    val rangeDays: Int,
    val activeDays: Int,
    val coverageRatio: Float,
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val totalMinutes: Long,
    val focusedMinutes: Long,
    val focusEfficiency: Float, // focus/total
    val avgMinutesPerActiveDay: Float,
    val avgMinutesPerCalendarDay: Float,

    val dailyCv: Float, // coefficient of variation của minutes/day
    val peakHour: Int?,
    val hourEntropy: Float?,
    val sessionLengthHistogram: List<Long>,
    val weekdayMinutes: List<Long>
)