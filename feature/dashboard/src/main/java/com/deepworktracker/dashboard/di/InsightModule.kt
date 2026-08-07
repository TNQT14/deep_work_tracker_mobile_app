package com.deepworktracker.dashboard.di

import com.deepworktracker.domain.insights.InsightRule
import com.deepworktracker.domain.insights.rules.BestFocusHoursRule
import com.deepworktracker.domain.insights.rules.DecliningTrendRule
import com.deepworktracker.domain.insights.rules.DistractionPatternRule
import com.deepworktracker.domain.insights.rules.OptimalSessionLengthRule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * [DI]
 * Wires the 4 concrete InsightRule implementations into a single List<InsightRule> so
 * GenerateInsightsUseCase can just iterate `rules` without knowing each rule by name.
 * Rules are plain `InsightRule()` constructors (not @Inject-annotated) because they
 * have zero dependencies — core:domain has no javax.inject on its classpath, so a
 * rule that ever needs a dependency would move to @Inject constructor + a parameter
 * here instead.
 */
@Module
@InstallIn(SingletonComponent::class)
object InsightModule {

    /**
     * Output: List<InsightRule> with all 4 rules in evaluation order (order doesn't
     * affect the result — GenerateInsightsUseCase runs every rule regardless of
     * whether an earlier one fired).
     */
    @Provides
    fun provideInsightRules(): List<InsightRule> = listOf(
        BestFocusHoursRule(),
        DistractionPatternRule(),
        OptimalSessionLengthRule(),
        DecliningTrendRule(),
    )
}