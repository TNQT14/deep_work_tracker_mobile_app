package com.deepworktracker.dashboard.presentation.category

import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.CategoryRule

data class CategoryUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val sessions: List<FocusSession> = emptyList(),
    val categories: List<CategorySummary> = emptyList(),
    val selectedCategory: String? = null,
    val selectedWeekTrendMinutes: List<Long> = emptyList(), // 12 weeks
    val selectedMonthTrendMinutes: List<Long> = emptyList(), // 12 months
    val selectedHeatmapMinutes: List<List<Long>> = emptyList(), // 7x24 (Mon..Sun, 0..23)
    val rules: List<CategoryRule> = emptyList(),
)

data class CategorySummary(
    val category: String,
    val totalMinutes: Long,
    val shareRatio: Float, // 0..1
)

