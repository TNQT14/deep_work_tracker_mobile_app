package com.deepworktracker.dashboard.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.model.CategoryRule
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.CategoryRuleRepository
import com.deepworktracker.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val categoryRuleRepository: CategoryRuleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState(isLoading = true))
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        observeRules()
        refresh()
    }

    private fun observeRules() {
        viewModelScope.launch {
            categoryRuleRepository.observeRules().collectLatest { rules ->
                _uiState.update { it.copy(rules = rules) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getAllSessionUseCase()) {
                is Result.Success -> {
                    val sessions = result.data.filter { !it.isActive }
                    val summaries = buildCategorySummaries(sessions)
                    val selected = _uiState.value.selectedCategory ?: summaries.firstOrNull()?.category
                    val computed = computeCategoryViews(sessions, selected)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sessions = sessions,
                            categories = summaries,
                            selectedCategory = selected,
                            selectedWeekTrendMinutes = computed.weekTrend,
                            selectedMonthTrendMinutes = computed.monthTrend,
                            selectedHeatmapMinutes = computed.heatmap,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.exception) }
                }
            }
        }
    }

    fun selectCategory(category: String) {
        val sessions = _uiState.value.sessions
        val computed = computeCategoryViews(sessions, category)
        _uiState.update {
            it.copy(
                selectedCategory = category,
                selectedWeekTrendMinutes = computed.weekTrend,
                selectedMonthTrendMinutes = computed.monthTrend,
                selectedHeatmapMinutes = computed.heatmap,
            )
        }
    }

    fun addRule(
        keyword: String?,
        startHour: Int?,
        endHour: Int?,
        category: String,
        tag: String?,
        priority: Int,
    ) {
        viewModelScope.launch {
            val rule = CategoryRule(
                id = UUID.randomUUID().toString(),
                keyword = keyword?.trim()?.takeIf { it.isNotBlank() },
                startHour = startHour,
                endHour = endHour,
                category = category.trim(),
                tag = tag?.trim()?.takeIf { it.isNotBlank() },
                priority = priority,
            )
            categoryRuleRepository.upsert(rule)
        }
    }

    fun deleteRule(id: String) {
        viewModelScope.launch {
            categoryRuleRepository.delete(id)
        }
    }
}

private data class CategoryViews(
    val weekTrend: List<Long>,
    val monthTrend: List<Long>,
    val heatmap: List<List<Long>>,
)

private fun buildCategorySummaries(sessions: List<FocusSession>): List<CategorySummary> {
    val totalMinutesAll = sessions.sumOf { it.totalDuration } / 60_000
    val byCategory = sessions.groupBy { it.category?.takeIf(String::isNotBlank) ?: "Uncategorized" }
        .mapValues { (_, ss) -> ss.sumOf { it.totalDuration } / 60_000 }

    return byCategory.entries
        .sortedByDescending { it.value }
        .map { (cat, minutes) ->
            CategorySummary(
                category = cat,
                totalMinutes = minutes,
                shareRatio = if (totalMinutesAll > 0) minutes.toFloat() / totalMinutesAll else 0f
            )
        }
}

private fun computeCategoryViews(sessions: List<FocusSession>, category: String?): CategoryViews {
    val cat = category?.takeIf { it.isNotBlank() } ?: "Uncategorized"
    val filtered = sessions.filter { (it.category?.takeIf(String::isNotBlank) ?: "Uncategorized") == cat }

    val zone = TimeZone.currentSystemDefault()

    // Week trend: last 12 Mondays (minutes per week)
    val mondayToMinutes = filtered.groupBy { s ->
        val d = s.startTime.toLocalDateTime(zone).date
        d.minus(d.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)
    }.mapValues { (_, ss) -> ss.sumOf { it.totalDuration } / 60_000 }

    val allMondays = sessions.map { s ->
        val d = s.startTime.toLocalDateTime(zone).date
        d.minus(d.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)
    }.distinct().sorted().takeLast(12)

    val weekTrend = allMondays.map { monday -> mondayToMinutes[monday] ?: 0L }

    // Month trend: last 12 months keys "YYYY-MM"
    val monthToMinutes = filtered.groupBy { s ->
        val dt = s.startTime.toLocalDateTime(zone)
        "${dt.year}-${dt.monthNumber.toString().padStart(2, '0')}"
    }.mapValues { (_, ss) -> ss.sumOf { it.totalDuration } / 60_000 }

    val allMonths = sessions.map { s ->
        val dt = s.startTime.toLocalDateTime(zone)
        "${dt.year}-${dt.monthNumber.toString().padStart(2, '0')}"
    }.distinct().sorted().takeLast(12)

    val monthTrend = allMonths.map { key -> monthToMinutes[key] ?: 0L }

    // Heatmap 7x24: Mon..Sun, hour 0..23 (minutes)
    val heat = List(7) { LongArray(24) }
    filtered.forEach { s ->
        val dt = s.startTime.toLocalDateTime(zone)
        val dowIdx = dt.date.dayOfWeek.ordinal.coerceIn(0, 6)
        heat[dowIdx][dt.hour.coerceIn(0, 23)] += s.totalDuration / 60_000
    }
    val heatmap = heat.map { row -> row.toList() }

    return CategoryViews(
        weekTrend = weekTrend,
        monthTrend = monthTrend,
        heatmap = heatmap
    )
}

