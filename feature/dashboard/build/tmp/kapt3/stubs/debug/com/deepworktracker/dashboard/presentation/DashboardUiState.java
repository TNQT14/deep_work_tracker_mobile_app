package com.deepworktracker.dashboard.presentation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0012\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001BG\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\u0002\u0010\fJ\u000b\u0010\u0015\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\tH\u00c6\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003JK\u0010\u001a\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\b\u001a\u00020\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000bH\u00c6\u0001J\u0013\u0010\u001b\u001a\u00020\t2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001J\t\u0010\u001f\u001a\u00020 H\u00d6\u0001R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0011R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000eR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006!"}, d2 = {"Lcom/deepworktracker/dashboard/presentation/DashboardUiState;", "", "todayStats", "Lcom/deepworktracker/dashboard/domain/usecase/TodayStats;", "recentSessions", "", "Lcom/deepworktracker/domain/model/FocusSession;", "allSessions", "isLoading", "", "error", "", "(Lcom/deepworktracker/dashboard/domain/usecase/TodayStats;Ljava/util/List;Ljava/util/List;ZLjava/lang/Throwable;)V", "getAllSessions", "()Ljava/util/List;", "getError", "()Ljava/lang/Throwable;", "()Z", "getRecentSessions", "getTodayStats", "()Lcom/deepworktracker/dashboard/domain/usecase/TodayStats;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "", "dashboard_debug"})
public final class DashboardUiState {
    @org.jetbrains.annotations.Nullable()
    private final com.deepworktracker.dashboard.domain.usecase.TodayStats todayStats = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.deepworktracker.domain.model.FocusSession> recentSessions = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.deepworktracker.domain.model.FocusSession> allSessions = null;
    private final boolean isLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Throwable error = null;
    
    public DashboardUiState(@org.jetbrains.annotations.Nullable()
    com.deepworktracker.dashboard.domain.usecase.TodayStats todayStats, @org.jetbrains.annotations.NotNull()
    java.util.List<com.deepworktracker.domain.model.FocusSession> recentSessions, @org.jetbrains.annotations.NotNull()
    java.util.List<com.deepworktracker.domain.model.FocusSession> allSessions, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.Throwable error) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.deepworktracker.dashboard.domain.usecase.TodayStats getTodayStats() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.deepworktracker.domain.model.FocusSession> getRecentSessions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.deepworktracker.domain.model.FocusSession> getAllSessions() {
        return null;
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Throwable getError() {
        return null;
    }
    
    public DashboardUiState() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.deepworktracker.dashboard.domain.usecase.TodayStats component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.deepworktracker.domain.model.FocusSession> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.deepworktracker.domain.model.FocusSession> component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Throwable component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.dashboard.presentation.DashboardUiState copy(@org.jetbrains.annotations.Nullable()
    com.deepworktracker.dashboard.domain.usecase.TodayStats todayStats, @org.jetbrains.annotations.NotNull()
    java.util.List<com.deepworktracker.domain.model.FocusSession> recentSessions, @org.jetbrains.annotations.NotNull()
    java.util.List<com.deepworktracker.domain.model.FocusSession> allSessions, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.Throwable error) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}