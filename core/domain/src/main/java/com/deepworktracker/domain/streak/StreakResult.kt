package com.deepworktracker.domain.streak

data  class StreakResult(
    val current: Int,
    val longest: Int,
    val isTodayDone: Boolean,
    val todayMinutes: Long,
){
    companion object {
        val EMPTY = StreakResult(current = 0, longest = 0, isTodayDone = false, todayMinutes = 0L)
    }
}