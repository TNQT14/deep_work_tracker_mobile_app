package com.deepworktracker.dashboard.domain.usecase;

/**
 * Data class để hiển thị stats hôm nay
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000eJB\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00052\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0007H\u00c6\u0001\u00a2\u0006\u0002\u0010\u001bJ\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020\u0007H\u00d6\u0001J\t\u0010 \u001a\u00020!H\u00d6\u0001R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0015\u0010\t\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\f\u00a8\u0006\""}, d2 = {"Lcom/deepworktracker/dashboard/domain/usecase/TodayStats;", "", "date", "Lkotlinx/datetime/LocalDate;", "totalFocusTime", "", "sessionCount", "", "averageSessionDuration", "bestFocusHour", "(Lkotlinx/datetime/LocalDate;JIJLjava/lang/Integer;)V", "getAverageSessionDuration", "()J", "getBestFocusHour", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getDate", "()Lkotlinx/datetime/LocalDate;", "getSessionCount", "()I", "getTotalFocusTime", "component1", "component2", "component3", "component4", "component5", "copy", "(Lkotlinx/datetime/LocalDate;JIJLjava/lang/Integer;)Lcom/deepworktracker/dashboard/domain/usecase/TodayStats;", "equals", "", "other", "hashCode", "toString", "", "dashboard_debug"})
public final class TodayStats {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.datetime.LocalDate date = null;
    private final long totalFocusTime = 0L;
    private final int sessionCount = 0;
    private final long averageSessionDuration = 0L;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer bestFocusHour = null;
    
    public TodayStats(@org.jetbrains.annotations.NotNull()
    kotlinx.datetime.LocalDate date, long totalFocusTime, int sessionCount, long averageSessionDuration, @org.jetbrains.annotations.Nullable()
    java.lang.Integer bestFocusHour) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.datetime.LocalDate getDate() {
        return null;
    }
    
    public final long getTotalFocusTime() {
        return 0L;
    }
    
    public final int getSessionCount() {
        return 0;
    }
    
    public final long getAverageSessionDuration() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getBestFocusHour() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.datetime.LocalDate component1() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final long component4() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.dashboard.domain.usecase.TodayStats copy(@org.jetbrains.annotations.NotNull()
    kotlinx.datetime.LocalDate date, long totalFocusTime, int sessionCount, long averageSessionDuration, @org.jetbrains.annotations.Nullable()
    java.lang.Integer bestFocusHour) {
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