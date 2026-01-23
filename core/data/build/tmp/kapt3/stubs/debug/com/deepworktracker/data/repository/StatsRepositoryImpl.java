package com.deepworktracker.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\u0018\u00002\u00020\u0001B\'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ$\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\u000fH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0018\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u000e\u001a\u00020\u000fH\u0096@\u00a2\u0006\u0002\u0010\u0011J$\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00160\u00152\u0006\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u000fH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0019"}, d2 = {"Lcom/deepworktracker/data/repository/StatsRepositoryImpl;", "Lcom/deepworktracker/domain/repository/StatsRepository;", "statsDao", "Lcom/deepworktracker/data/database/dao/StatsDao;", "sessionDao", "Lcom/deepworktracker/data/database/dao/SessionDao;", "statsMapper", "Lcom/deepworktracker/data/mapper/StatsMapper;", "sessionMapper", "Lcom/deepworktracker/data/mapper/SessionMapper;", "(Lcom/deepworktracker/data/database/dao/StatsDao;Lcom/deepworktracker/data/database/dao/SessionDao;Lcom/deepworktracker/data/mapper/StatsMapper;Lcom/deepworktracker/data/mapper/SessionMapper;)V", "calculateAndSaveDailyStats", "Lkotlin/Result;", "", "date", "Lkotlinx/datetime/LocalDate;", "calculateAndSaveDailyStats-gIAlu-s", "(Lkotlinx/datetime/LocalDate;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDailyStats", "Lcom/deepworktracker/domain/model/DailyStats;", "getStatsByDateRange", "Lkotlinx/coroutines/flow/Flow;", "", "startDate", "endDate", "data_debug"})
public final class StatsRepositoryImpl implements com.deepworktracker.domain.repository.StatsRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.data.database.dao.StatsDao statsDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.data.database.dao.SessionDao sessionDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.data.mapper.StatsMapper statsMapper = null;
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.data.mapper.SessionMapper sessionMapper = null;
    
    @javax.inject.Inject()
    public StatsRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.dao.StatsDao statsDao, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.dao.SessionDao sessionDao, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.mapper.StatsMapper statsMapper, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.mapper.SessionMapper sessionMapper) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getDailyStats(@org.jetbrains.annotations.NotNull()
    kotlinx.datetime.LocalDate date, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.deepworktracker.domain.model.DailyStats> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.deepworktracker.domain.model.DailyStats>> getStatsByDateRange(@org.jetbrains.annotations.NotNull()
    kotlinx.datetime.LocalDate startDate, @org.jetbrains.annotations.NotNull()
    kotlinx.datetime.LocalDate endDate) {
        return null;
    }
}