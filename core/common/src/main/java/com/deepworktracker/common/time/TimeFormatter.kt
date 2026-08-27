package com.deepworktracker.common.time

import kotlin.time.Duration
import kotlin.time.DurationUnit

object TimeFormatter {
    
    fun formatDuration(duration: Duration): String {
        val totalSeconds = duration.inWholeSeconds
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return buildList {
            if (hours > 0) add("${hours}h")
            if (minutes > 0) add("${minutes}p")
            if (seconds > 0 || isEmpty()) add("${seconds}s")
        }.joinToString(" ")
    }
    
    fun formatDurationShort(duration: Duration): String {
        val totalMinutes = duration.inWholeMinutes
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }
}
