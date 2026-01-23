package com.deepworktracker.dashboard.presentation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000D\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\"\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u0010\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\bH\u0007\u001a\"\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u001a\u0012\u0010\u000f\u001a\u00020\u00012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0007\u001a\u000e\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u0014\u001a\u000e\u0010\u0015\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u0017\u00a8\u0006\u0018"}, d2 = {"DashboardScreen", "", "viewModel", "Lcom/deepworktracker/dashboard/presentation/DashboardViewModel;", "onNavigateToSession", "Lkotlin/Function0;", "SessionCard", "session", "Lcom/deepworktracker/domain/model/FocusSession;", "StatRow", "label", "", "value", "modifier", "Landroidx/compose/ui/Modifier;", "TodayStatsCard", "stats", "Lcom/deepworktracker/dashboard/domain/usecase/TodayStats;", "formatHour", "hour", "", "formatSessionDate", "instant", "Lkotlinx/datetime/Instant;", "dashboard_debug"})
public final class DashboardScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void DashboardScreen(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.dashboard.presentation.DashboardViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToSession) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void TodayStatsCard(@org.jetbrains.annotations.Nullable()
    com.deepworktracker.dashboard.domain.usecase.TodayStats stats) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StatRow(@org.jetbrains.annotations.NotNull()
    java.lang.String label, @org.jetbrains.annotations.NotNull()
    java.lang.String value, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SessionCard(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.domain.model.FocusSession session) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String formatHour(int hour) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String formatSessionDate(@org.jetbrains.annotations.NotNull()
    kotlinx.datetime.Instant instant) {
        return null;
    }
}