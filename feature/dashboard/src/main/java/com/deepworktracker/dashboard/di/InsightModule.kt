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

@Module
@InstallIn(SingletonComponent::class)
object InsightModule {

    @Provides
    fun provideInsightRules(): List<InsightRule> = listOf(
        BestFocusHoursRule(),
        DistractionPatternRule(),
        OptimalSessionLengthRule(),
        DecliningTrendRule(),
    )
}