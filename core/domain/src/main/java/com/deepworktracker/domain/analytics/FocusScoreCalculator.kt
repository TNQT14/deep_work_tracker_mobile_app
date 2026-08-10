package com.deepworktracker.domain.analytics

import com.deepworktracker.domain.model.FocusSession

object FocusScoreCalculator {
    fun score(sessions: List<FocusSession>): Float{
        val completed = sessions.filter {it.endTime != null}

        var totalDuration = 0L
        var totalFocused = 0L
        for(session in completed){
            totalFocused += session.focusedDuration
            totalDuration += session.totalDuration
        }

        if (totalDuration <= 0L) return 0f
        return (totalFocused.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    }
}