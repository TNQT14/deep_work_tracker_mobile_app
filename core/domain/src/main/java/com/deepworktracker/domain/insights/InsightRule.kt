package com.deepworktracker.domain.insights

import com.deepworktracker.domain.analytics.AnalyticsPeriod
import com.deepworktracker.domain.analytics.FocusAnalytics
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.Insight
import com.deepworktracker.domain.model.InsightType
import kotlinx.datetime.Instant
import java.util.UUID


/**
 * [Domain — Insight Engine]
 * Pre-computed snapshot of one analytics period, handed to every [InsightRule].
 * Building this once (in GenerateInsightsUseCase) instead of letting each rule
 * query its own data keeps rules pure (no I/O) and trivial to unit test.
 *
 * Fields:
 * - period: AnalyticsPeriod — e.g. AnalyticsPeriod.WEEK
 * - current: FocusAnalytics — this period's aggregates (score, heatmap, bestFocusHours...)
 * - previous: FocusAnalytics? — same aggregates for the immediately preceding period,
 *   null when there isn't enough history yet. Only DecliningTrendRule reads this.
 * - sessions: List<FocusSession> — raw completed/active sessions in [period], used by
 *   rules that need per-session detail (e.g. OptimalSessionLengthRule bucketing)
 * - interruptionsByDay: Map<Int, Int> — isoDayNumber (1=Mon..7=Sun) -> interruption count,
 *   e.g. {1: 2, 3: 7} means Wednesday had 7 interruptions this period
 * - now: Instant — timestamp stamped onto any Insight this window produces
 */
data class StatsWindow(
    val period: AnalyticsPeriod,
    val current: FocusAnalytics,
    val previous: FocusAnalytics?,
    val sessions: List<FocusSession>,
    val interruptionsByDay: Map<Int, Int>,
    val now: Instant
) {
    /**
     * Guardrail: below this many completed sessions, no rule should fire — insights
     * generated from too little data are more likely to be wrong or misleading.
     * Sample: current.sessionCount=3 -> hasEnoughData=false, engine skips this window entirely.
     */
    val hasEnoughData: Boolean get() = current.sessionCount >= MIN_SESSIONS

    companion object {
        const val MIN_SESSIONS = 5
    }
}

/**
 * [Domain — Insight Engine]
 * Single-method contract for one insight rule (Strategy pattern). Implementations are
 * pure: given the same [StatsWindow] they must return the same result, no side effects.
 * Returning null means "this rule does not apply to this window" (not an error).
 */
fun interface InsightRule {
    fun evaluate(window: StatsWindow): Insight?
}

/**
 * [Domain — Insight Engine]
 * Input: type (InsightType enum), fallbackMessage (English, used only if the UI can't
 *        render a localized string from `data`), now (Instant to stamp), data (a flat
 *        Map<String, Any> of parameters the UI needs to build its message, e.g.
 *        {"hours": "9,10", "count": 2})
 * Process: generates a fresh random id and assembles an [Insight].
 * Output: Insight with confidence = null — this is the flag that marks it as
 *         rule-based rather than AI-generated (AI-based insights, added later by
 *         feature #9, will carry a non-null confidence score).
 */
internal fun buildInsight(
    type: InsightType,
    fallbackMessage: String,
    now: Instant,
    data: Map<String, Any>
): Insight = Insight(
    id = UUID.randomUUID().toString(),
    type = type,
    message = fallbackMessage,
    generatedAt = now,
    confidence = null,
    data = data
)
