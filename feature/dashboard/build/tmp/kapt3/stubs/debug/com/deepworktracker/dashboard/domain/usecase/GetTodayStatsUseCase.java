package com.deepworktracker.dashboard.domain.usecase;

/**
 * Use case để lấy thống kê hôm nay
 * Nếu chưa có stats được tính toán, sẽ tính toán từ sessions
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0086B\u00a2\u0006\u0002\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/deepworktracker/dashboard/domain/usecase/GetTodayStatsUseCase;", "", "sessionRepository", "Lcom/deepworktracker/domain/repository/SessionRepository;", "statsRepository", "Lcom/deepworktracker/domain/repository/StatsRepository;", "(Lcom/deepworktracker/domain/repository/SessionRepository;Lcom/deepworktracker/domain/repository/StatsRepository;)V", "invoke", "Lcom/deepworktracker/common/result/Result;", "Lcom/deepworktracker/dashboard/domain/usecase/TodayStats;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "dashboard_debug"})
public final class GetTodayStatsUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.domain.repository.SessionRepository sessionRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.domain.repository.StatsRepository statsRepository = null;
    
    @javax.inject.Inject()
    public GetTodayStatsUseCase(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.domain.repository.SessionRepository sessionRepository, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.domain.repository.StatsRepository statsRepository) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object invoke(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.deepworktracker.common.result.Result<com.deepworktracker.dashboard.domain.usecase.TodayStats>> $completion) {
        return null;
    }
}