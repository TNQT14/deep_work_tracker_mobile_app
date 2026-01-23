package com.deepworktracker.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J$\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000bH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u0018\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\n\u001a\u00020\u000bH\u0096@\u00a2\u0006\u0002\u0010\rJ\u001c\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00120\u00112\u0006\u0010\n\u001a\u00020\u000bH\u0016J$\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u0014\u001a\u00020\u000fH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0015\u0010\u0016J$\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u0014\u001a\u00020\u000fH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0018\u0010\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0019"}, d2 = {"Lcom/deepworktracker/data/repository/InterruptionRepositoryImpl;", "Lcom/deepworktracker/domain/repository/InterruptionRepository;", "interruptionDao", "Lcom/deepworktracker/data/database/dao/InterruptionDao;", "mapper", "Lcom/deepworktracker/data/mapper/InterruptionMapper;", "(Lcom/deepworktracker/data/database/dao/InterruptionDao;Lcom/deepworktracker/data/mapper/InterruptionMapper;)V", "deleteInterruptionsBySession", "Lkotlin/Result;", "", "sessionId", "", "deleteInterruptionsBySession-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getActiveInterruption", "Lcom/deepworktracker/domain/model/Interruption;", "getInterruptionsBySession", "Lkotlinx/coroutines/flow/Flow;", "", "saveInterruption", "interruption", "saveInterruption-gIAlu-s", "(Lcom/deepworktracker/domain/model/Interruption;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateInterruption", "updateInterruption-gIAlu-s", "data_debug"})
public final class InterruptionRepositoryImpl implements com.deepworktracker.domain.repository.InterruptionRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.data.database.dao.InterruptionDao interruptionDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.data.mapper.InterruptionMapper mapper = null;
    
    @javax.inject.Inject()
    public InterruptionRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.dao.InterruptionDao interruptionDao, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.mapper.InterruptionMapper mapper) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.deepworktracker.domain.model.Interruption>> getInterruptionsBySession(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getActiveInterruption(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.deepworktracker.domain.model.Interruption> $completion) {
        return null;
    }
}