package com.deepworktracker.domain.model

import kotlinx.datetime.Instant

data class Insight(
    val id: String,
    val type: InsightType,
    val message: String,
    val generatedAt: Instant,
    val confidence: Float? = null, // 0.0 - 1.0, null for rule-based
    val data: Map<String, Any>? = null
)

enum class InsightType {
    BEST_TIME_WINDOW,      // "You focus best from 8:45 to 10:15"
    OPTIMAL_SESSION_LENGTH, // "After 26 minutes, interruption risk increases"
    DISTRACTION_PATTERN,    // "You're most distracted on Mondays"
    PRODUCTIVITY_TREND      // "Your focus time increased 15% this week"
}
