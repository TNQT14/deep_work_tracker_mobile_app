package com.deepworktracker.domain.streak

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

object StreakCalculator {
    fun calculate(
        dailyMinutes: Map<LocalDate, Long>,
        goalMinutes: Int,
        today: LocalDate,
    ): StreakResult{
        val todayMinutes = dailyMinutes[today] ?: 0L

        if (goalMinutes <=0){
            return StreakResult.EMPTY.copy(
                todayMinutes = todayMinutes
            )
        }

        val isTodayDone = todayMinutes >= goalMinutes

        var current = if(isTodayDone) 1 else 0
        var cursor = today.minus(1, DateTimeUnit.DAY)
        while ((dailyMinutes[cursor] ?: 0L) >= goalMinutes){
            current++
            cursor = cursor.minus(1, DateTimeUnit.DAY)
        }

        var longest = 0
        val earliest = dailyMinutes.keys.minOrNull()
        if (earliest != null) {
            var run = 0
            var day: LocalDate = earliest
            while (day <= today) {
                if ((dailyMinutes[day] ?: 0L) >= goalMinutes) {
                    run++
                    if (run > longest) longest = run
                } else {
                    run = 0
                }
                day = day.plus(1, DateTimeUnit.DAY)
            }
        }

        return StreakResult(
            current = current,
            longest = maxOf(longest, current),
            isTodayDone = isTodayDone,
            todayMinutes = todayMinutes,
        )
    }
}