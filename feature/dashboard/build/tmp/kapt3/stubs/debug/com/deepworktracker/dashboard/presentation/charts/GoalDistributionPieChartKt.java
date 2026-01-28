package com.deepworktracker.dashboard.presentation.charts;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0007\u001a\u0010\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u0007H\u0007\u001a\u0013\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\f\u00a8\u0006\r"}, d2 = {"GoalDistributionChart", "", "sessions", "", "Lcom/deepworktracker/domain/model/FocusSession;", "GoalDistributionRow", "goalData", "Lcom/deepworktracker/dashboard/presentation/charts/GoalData;", "getColorForGoal", "Landroidx/compose/ui/graphics/Color;", "goal", "", "(Ljava/lang/String;)J", "dashboard_debug"})
public final class GoalDistributionPieChartKt {
    
    /**
     * Pie Chart hiển thị phân bố focus time theo loại goal
     * Vico không có Pie Chart built-in, nên dùng Canvas hoặc thư viện khác
     * Hoặc dùng simple implementation với progress indicators
     */
    @androidx.compose.runtime.Composable()
    public static final void GoalDistributionChart(@org.jetbrains.annotations.NotNull()
    java.util.List<com.deepworktracker.domain.model.FocusSession> sessions) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GoalDistributionRow(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.dashboard.presentation.charts.GoalData goalData) {
    }
    
    public static final long getColorForGoal(@org.jetbrains.annotations.NotNull()
    java.lang.String goal) {
        return 0L;
    }
}