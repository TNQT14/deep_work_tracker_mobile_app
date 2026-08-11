package com.deepworktracker.dashboard.presentation.goal_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.plus
import javax.inject.Inject
import kotlin.math.ln
import kotlin.math.sqrt

@HiltViewModel
class GoalDetailViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalDetailUiState())
    val uiState: StateFlow<GoalDetailUiState> = _uiState.asStateFlow()

    fun loadGoal(goal: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                )

                val today = Clock.System.now()
                    .toLocalDateTime(TimeZone.Companion.currentSystemDefault()).date
                val onYearAgo = today.minus(1, DateTimeUnit.YEAR)

                val list = sessionRepository.getSessionsByDateRange(onYearAgo, today).first()
                val filtered = list.filter { it.goal == goal }.sortedByDescending { it.startTime }
                val total = filtered.sumOf { it.totalDuration }

                val zone = TimeZone.currentSystemDefault()

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
                        val monday = date.minus((date.dayOfWeek.ordinal).toLong(), DateTimeUnit.DAY)
                        weekKey(monday)
                    }
                    .mapValues { (_, sessions) -> sessions.sumOf { it.totalDuration } }
                val weekOrder = sortedDays
                    .map { d -> d.minus((d.dayOfWeek.ordinal).toLong(), DateTimeUnit.DAY) }
                    .distinct()
                    .sorted()
                    .takeLast(12)
                    .map { monday -> weekKey(monday) }
                val byWeek = weekOrder.map { key -> key to (byWeekMap[key] ?: 0L) }

                val byMonthMap = filtered
                    .groupBy {
                        it.startTime.toLocalDateTime(zone).let { "${it.year}-${it.monthNumber}" }
                    }
                    .mapValues { (_, sessions) -> sessions.sumOf { it.totalDuration } }
                val monthOrder =
                    sortedDays.map { "${it.year}-${it.month.number}" }.distinct().takeLast(12)
                val byMonth = monthOrder.map { key -> key to (byMonthMap[key] ?: 0L) }

                val byYearMap = filtered
                    .groupBy { it.startTime.toLocalDateTime(zone).year }
                    .mapValues { (_, sessions) -> sessions.sumOf { it.totalDuration } }
                val yearOrder = byYearMap.keys.sorted()
                val byYear = yearOrder.map { y -> y to (byYearMap[y] ?: 0L) }

                val metricsStart = today.minus(89, DateTimeUnit.DAY) // last 90 days inclusive
                val metrics = computeGoalMetrics(
                    sessions = filtered,
                    zone = zone,
                    startDate = metricsStart,
                    endDate = today,
                )

                _uiState.value = GoalDetailUiState(
                    sessions = filtered,
                    totalDuration = total,
                    isLoading = false,
                    error = null,
                    chartByHour = byHour,
                    chartByDay = byDay,
                    chartByWeek = byWeek,
                    chartByMonth = byMonth,
                    chartByYear = byYear,
                    metrics = metrics,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }
}

private fun computeGoalMetrics(
    sessions: List<com.deepworktracker.domain.model.FocusSession>,
    zone: TimeZone,
    startDate: kotlinx.datetime.LocalDate,
    endDate: kotlinx.datetime.LocalDate,
): GoalMetrics {
    val rangeDays = daysBetweenInclusive(startDate, endDate)
    val completed = sessions.filter { it.endTime != null }

    val totalMs = completed.sumOf { it.totalDuration }.coerceAtLeast(0L)
    val focusedMs = completed.sumOf { it.focusedDuration }.coerceAtLeast(0L)
    val totalMinutes = totalMs / 60_000
    val focusedMinutes = focusedMs / 60_000
    val focusEfficiency = if (totalMs > 0L) focusedMs.toFloat() / totalMs else 0f

    val byDateMs = completed
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

    val streak = StreakCalculator.calculate(
        dailyMinutes = byDateMs.mapValues { (_, ms) ->
            ms / 60_000
        },
        goalMinutes = 1,
        today = endDate
    )
    val avgMinutesPerActiveDay = if (activeDays > 0) totalMinutes.toFloat() / activeDays else 0f
    val avgMinutesPerCalendarDay = if (rangeDays > 0) totalMinutes.toFloat() / rangeDays else 0f
    val dailyCv = coefficientOfVariation(dailyMinutes.map { it.toFloat() })

    val byHourMinutes = (0..23).map { hour ->
        completed
            .filter { it.startTime.toLocalDateTime(zone).hour == hour }
            .sumOf { it.totalDuration } / 60_000
    }
    val peakHour = byHourMinutes
        .withIndex()
        .maxByOrNull { it.value }
        ?.takeIf { it.value > 0L }
        ?.index
    val hourEntropy = shannonEntropy(byHourMinutes.map { it.toFloat() })

    val weekdayMinutes = LongArray(7) // Mon..Sun
    completed.forEach { s ->
        val dow = s.startTime.toLocalDateTime(zone).date.dayOfWeek
        val idx = dow.ordinal.coerceIn(0, 6)
        weekdayMinutes[idx] += s.totalDuration / 60_000
    }

    val sessionLengthHistogram = histogram5Buckets(completed.map { it.totalDuration / 60_000 })

    return GoalMetrics(
        rangeDays = rangeDays,
        activeDays = activeDays,
        coverageRatio = coverageRatio,
        currentStreakDays = if(streak.isTodayDone) streak.current else 0,
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

private fun daysBetweenInclusive(
    start: kotlinx.datetime.LocalDate,
    end: kotlinx.datetime.LocalDate
): Int {
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

private fun legacyComputeStreaks(dailyMinutes: List<Long>): Pair<Int, Int> {
    var current = 0
    var best = 0
    for (m in dailyMinutes) {
        if (m > 0) {
            current++
            if (current > best) best = current
        } else {
            current = 0
        }
    }
    var suffix = 0
    for (i in dailyMinutes.indices.reversed()) {
        if (dailyMinutes[i] > 0) suffix++ else break
    }
    return suffix to best
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
    // buckets: [0-15), [15-30), [30-60), [60-90), [90+]
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

private fun weekKey(monday: kotlinx.datetime.LocalDate): String {
    val w = isoWeekNumber(monday)
    val ww = w.toString().padStart(2, '0')
    return "${monday.year}-W$ww"
}

/**
 * ISO week number approximation based on week starting Monday.
 * We use the "week's Monday" date as representative; this is stable enough for charting.
 */
private fun isoWeekNumber(monday: kotlinx.datetime.LocalDate): Int {
    // Take Thursday of this week to determine ISO week-year and week number.
    val thursday = monday.plus(3, DateTimeUnit.DAY)
    val jan4 = kotlinx.datetime.LocalDate(thursday.year, 1, 4)
    val jan4Monday = jan4.minus((jan4.dayOfWeek.ordinal).toLong(), DateTimeUnit.DAY)
    val days = daysBetweenInclusive(jan4Monday, monday) - 1
    return (days / 7) + 1
}