package com.deepworktracker.dashboard.presentation.charts;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a,\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0014\b\u0002\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u0006H\u0007\u001a \u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\n2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0007\u001a\u0013\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\u0010\u00a8\u0006\u0011"}, d2 = {"GoalDistributionChart", "", "sessions", "", "Lcom/deepworktracker/domain/model/FocusSession;", "onGoalClick", "Lkotlin/Function1;", "", "GoalDistributionRow", "goalData", "Lcom/deepworktracker/dashboard/presentation/charts/GoalData;", "onClick", "Lkotlin/Function0;", "getColorForGoal", "Landroidx/compose/ui/graphics/Color;", "goal", "(Ljava/lang/String;)J", "dashboard_debug"})
public final class GoalDistributionPieChartKt {
    
    /**
     * Pie Chart hiển thị phân bố focus time theo loại goal
     * Vico không có Pie Chart built-in, nên dùng Canvas hoặc thư viện khác
     * Hoặc dùng simple implementation với progress indicators
     */
    @androidx.compose.runtime.Composable()
    public static final void GoalDistributionChart(@org.jetbrains.annotations.NotNull()
    java.util.List<com.deepworktracker.domain.model.FocusSession> sessions, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onGoalClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GoalDistributionRow(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.dashboard.presentation.charts.GoalData goalData, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    public static final long getColorForGoal(@org.jetbrains.annotations.NotNull()
    java.lang.String goal) {
        return 0L;
    }
}