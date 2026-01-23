package com.deepworktracker.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J$\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000bH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u0018\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\u000bH\u0096@\u00a2\u0006\u0002\u0010\rJ\u001c\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00130\u00122\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J$\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u0017\u001a\u00020\u000fH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u001a"}, d2 = {"Lcom/deepworktracker/data/repository/InsightRepositoryImpl;", "Lcom/deepworktracker/domain/repository/InsightRepository;", "insightDao", "Lcom/deepworktracker/data/database/dao/InsightDao;", "mapper", "Lcom/deepworktracker/data/mapper/InsightMapper;", "(Lcom/deepworktracker/data/database/dao/InsightDao;Lcom/deepworktracker/data/mapper/InsightMapper;)V", "dismissInsight", "Lkotlin/Result;", "", "id", "", "dismissInsight-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLatestInsightByType", "Lcom/deepworktracker/domain/model/Insight;", "type", "getRecentInsights", "Lkotlinx/coroutines/flow/Flow;", "", "limit", "", "saveInsight", "insight", "saveInsight-gIAlu-s", "(Lcom/deepworktracker/domain/model/Insight;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "data_debug"})
public final class InsightRepositoryImpl implements com.deepworktracker.domain.repository.InsightRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.data.database.dao.InsightDao insightDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.data.mapper.InsightMapper mapper = null;
    
    @javax.inject.Inject()
    public InsightRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.dao.InsightDao insightDao, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.mapper.InsightMapper mapper) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.deepworktracker.domain.model.Insight>> getRecentInsights(int limit) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getLatestInsightByType(@org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.deepworktracker.domain.model.Insight> $completion) {
        return null;
    }
}