package com.deepworktracker.dashboard.presentation.category_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.dashboard.presentation.goal_detail.GoalMetrics
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import com.deepworktracker.domain.streak.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.math.ln
import kotlin.math.sqrt

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryDetailUiState())
    val uiState: StateFlow<CategoryDetailUiState> = _uiState.asStateFlow()

    fun loadCategory(category: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val zone = TimeZone.currentSystemDefault()
                val today = Clock.System.now().toLocalDateTime(zone).date
                val oneYearAgo = today.minus(365, DateTimeUnit.DAY)

                val list = sessionRepository.getSessionsByDateRange(oneYearAgo, today).first()
                val filtered = list
                    .filter { !it.isActive }
                    .filter { normalizeCategory(it.category) == normalizeCategory(category) }
                    .sortedByDescending { it.startTime }

                val total = filtered.sumOf { it.totalDuration }

                val byHour = (0..23).map { hour ->
                    filtered
                        .filter { it.startTime.toLocalDateTime(zone).hour == hour }
                        .sumOf { it.totalDuration }
                }

                val byDayMap = filtered
                    .groupBy { it.startTime.toLocalDateTime(zone).date }
                    .mapValues { (_, sessions) -> sessions.sumOf { it.totalDuration } }
                val sortedDays = byDayMap.keys.sorted()
                val byDay = sortedDays.takeLast(30).map { d -> d to (byDayMap[d] ?: 0L) }

                val byWeekMap = filtered
                    .groupBy { session ->
                        val date = session.startTime.toLocalDateTime(zone).date
                        val monday = date.minus(date.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)
                        weekKey(monday)
                    }
                    .mapValues { (_, sessions) -> sessions.sumOf { it.totalDuration } }
                val weekOrder = sortedDays
                    .map { d -> d.minus(d.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY) }
                    .distinct()
                    .sorted()
                    .takeLast(12)
                    .map { monday -> weekKey(monday) }
                val byWeek = weekOrder.map { key -> key to (byWeekMap[key] ?: 0L) }

                val byMonthMap = filtered
                    .groupBy { it.startTime.toLocalDateTime(zone).let { dt -> "${dt.year}-${dt.monthNumber}" } }
                    .mapValues { (_, sessions) -> sessions.sumOf { it.totalDuration } }
                val monthOrder = sortedDays
                    .map { d -> "${d.year}-${d.monthNumber.toString().padStart(2, '0')}" }
                    .distinct()
                    .takeLast(12)
                val byMonth = monthOrder.map { key -> key to (byMonthMap[key] ?: 0L) }

                val metricsStart = today.minus(89, DateTimeUnit.DAY)
                val metrics = computeMetrics(
                    sessions = filtered,
                    zone = zone,
                    startDate = metricsStart,
                    endDate = today,
                )

                val compare = compute30dCompare(filtered, zone, today)

                _uiState.value = CategoryDetailUiState(
                    sessions = filtered,
                    totalDuration = total,
                    isLoading = false,
                    error = null,
                    chartByHour = byHour,
                    chartByDay = byDay,
                    chartByWeek = byWeek,
                    chartByMonth = byMonth,
                    metrics = metrics,
                    compare30d = compare,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }
}

private fun normalizeCategory(raw: String?): String {
    return raw?.trim().takeUnless { it.isNullOrEmpty() } ?: "Uncategorized"
}

private fun compute30dCompare(
    sessions: List<FocusSession>,
    zone: TimeZone,
    today: LocalDate,
): PeriodCompare {
    val currentStart = today.minus(29, DateTimeUnit.DAY)
    val previousStart = today.minus(59, DateTimeUnit.DAY)
    val previousEnd = today.minus(30, DateTimeUnit.DAY)

    fun inRange(date: LocalDate, start: LocalDate, end: LocalDate): Boolean = date >= start && date <= end

    val currentMinutes = sessions
        .filter { inRange(it.startTime.toLocalDateTime(zone).date, currentStart, today) }
        .sumOf { it.totalDuration } / 60_000
    val previousMinutes = sessions
        .filter { inRange(it.startTime.toLocalDateTime(zone).date, previousStart, previousEnd) }
        .sumOf { it.totalDuration } / 60_000

    val deltaRatio = if (previousMinutes > 0) {
        (currentMinutes - previousMinutes).toFloat() / previousMinutes
    } else {
        if (currentMinutes > 0) 1f else 0f
    }

    return PeriodCompare(
        currentMinutes = currentMinutes,
        previousMinutes = previousMinutes,
        deltaRatio = deltaRatio
    )
}

private fun computeMetrics(
    sessions: List<FocusSession>,
    zone: TimeZone,
    startDate: LocalDate,
    endDate: LocalDate,
): GoalMetrics {
    val rangeDays = daysBetweenInclusive(startDate, endDate)

    val totalMs = sessions.sumOf { it.totalDuration }.coerceAtLeast(0L)
    val focusedMs = sessions.sumOf { it.focusedDuration }.coerceAtLeast(0L)
    val totalMinutes = totalMs / 60_000
    val focusedMinutes = focusedMs / 60_000
    val focusEfficiency = if (totalMs > 0L) focusedMs.toFloat() / totalMs else 0f

    val byDateMs = sessions
        .groupBy { it.startTime.toLocalDateTime(zone).date }
        .mapValues { (_, ss) -> ss.sumOf { it.totalDuration } }

    val activeDays = byDateMs.size
    val coverageRatio = if (rangeDays > 0) activeDays.toFloat() / rangeDays else 0f

    val dailyMinutes = buildList(rangeDays.coerceAtLeast(0)) {
        var d = startDate
        repeat(rangeDays) {
            add((byDateMs[d] ?: 0L) / 60_000)
            d = d.plus(1, DateTimeUnit.DAY)
        }
    }

    // Streak dùng chung StreakCalculator (core/domain).
    // goalMinutes = 1 giữ nguyên ngưỡng cũ "ngày có hoạt động" (> 0 phút).
    // byDateMs là mili-giây → đổi sang phút cho khớp đơn vị của domain.
    val streak = StreakCalculator.calculate(
        dailyMinutes = byDateMs.mapValues { (_, ms) -> ms / 60_000 },
        goalMinutes = 1,
        today = endDate,
    )
    val avgMinutesPerActiveDay = if (activeDays > 0) totalMinutes.toFloat() / activeDays else 0f
    val avgMinutesPerCalendarDay = if (rangeDays > 0) totalMinutes.toFloat() / rangeDays else 0f
    val dailyCv = coefficientOfVariation(dailyMinutes.map { it.toFloat() })

    val byHourMinutes = (0..23).map { hour ->
        sessions
            .filter { it.startTime.toLocalDateTime(zone).hour == hour }
            .sumOf { it.totalDuration } / 60_000
    }
    val peakHour = byHourMinutes
        .withIndex()
        .maxByOrNull { it.value }
        ?.takeIf { it.value > 0L }
        ?.index
    val hourEntropy = shannonEntropy(byHourMinutes.map { it.toFloat() })

    val weekdayMinutes = LongArray(7)
    sessions.forEach { s ->
        val dow = s.startTime.toLocalDateTime(zone).date.dayOfWeek
        val idx = dow.ordinal.coerceIn(0, 6)
        weekdayMinutes[idx] += s.totalDuration / 60_000
    }

    val sessionLengthHistogram = histogram5Buckets(sessions.map { it.totalDuration / 60_000 })

    return GoalMetrics(
        rangeDays = rangeDays,
        activeDays = activeDays,
        coverageRatio = coverageRatio,
        // Màn hình này vốn dùng suffix streak nghiêm ngặt: ngày cuối range không có
        // hoạt động thì chuỗi = 0. StreakCalculator cố ý khoan dung với "hôm nay",
        // nên quy đổi lại để con số hiển thị không đổi so với trước refactor.
        currentStreakDays = if (streak.isTodayDone) streak.current else 0,
        longestStreakDays = streak.longest,
        totalMinutes = totalMinutes,
        focusedMinutes = focusedMinutes,
        focusEfficiency = focusEfficiency,
        avgMinutesPerActiveDay = avgMinutesPerActiveDay,
        avgMinutesPerCalendarDay = avgMinutesPerCalendarDay,
        dailyCv = dailyCv,
        peakHour = peakHour,
        hourEntropy = hourEntropy,
        sessionLengthHistogram = sessionLengthHistogram,
        weekdayMinutes = weekdayMinutes.toList(),
    )
}

private fun daysBetweenInclusive(start: LocalDate, end: LocalDate): Int {
    if (end < start) return 0
    var d = start
    var count = 1
    while (d != end) {
        d = d.plus(1, DateTimeUnit.DAY)
        count++
        if (count > 5000) break
    }
    return count
}

private fun coefficientOfVariation(xs: List<Float>): Float {
    if (xs.isEmpty()) return 0f
    val mean = xs.average().toFloat()
    if (mean <= 0f) return 0f
    val variance = xs.map { (it - mean) * (it - mean) }.average().toFloat()
    val std = sqrt(variance)
    return std / mean
}

private fun shannonEntropy(weights: List<Float>): Float? {
    val sum = weights.sum()
    if (sum <= 0f) return null
    var h = 0f
    for (w in weights) {
        if (w <= 0f) continue
        val p = w / sum
        h += (-p * ln(p))
    }
    return h
}

private fun histogram5Buckets(durationsMinutes: List<Long>): List<Long> {
    val b = LongArray(5)
    durationsMinutes.forEach { m ->
        val idx = when {
            m < 15 -> 0
            m < 30 -> 1
            m < 60 -> 2
            m < 90 -> 3
            else -> 4
        }
        b[idx]++
    }
    return b.toList()
}

private fun weekKey(monday: LocalDate): String {
    val ww = isoWeekNumber(monday).toString().padStart(2, '0')
    return "${monday.year}-W$ww"
}

private fun isoWeekNumber(monday: LocalDate): Int {
    val thursday = monday.plus(3, DateTimeUnit.DAY)
    val jan4 = LocalDate(thursday.year, 1, 4)
    val jan4Monday = jan4.minus(jan4.dayOfWeek.ordinal.toLong(), DateTimeUnit.DAY)
    val days = daysBetweenInclusive(jan4Monday, monday) - 1
    return (days / 7) + 1
}

