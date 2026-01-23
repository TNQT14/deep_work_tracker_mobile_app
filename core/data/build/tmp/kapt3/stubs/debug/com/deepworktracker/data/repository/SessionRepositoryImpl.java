package com.deepworktracker.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J$\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000bH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0096@\u00a2\u0006\u0002\u0010\u0010J\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0012H\u0096@\u00a2\u0006\u0002\u0010\u0010J\u0018\u0010\u0013\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\n\u001a\u00020\u000bH\u0096@\u00a2\u0006\u0002\u0010\rJ\u001c\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00120\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J$\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00120\u00152\u0006\u0010\u0019\u001a\u00020\u00172\u0006\u0010\u001a\u001a\u00020\u0017H\u0016J\u0010\u0010\u001b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000f0\u0015H\u0016J$\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u001d\u001a\u00020\u000fH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001e\u0010\u001fJ$\u0010 \u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u001d\u001a\u00020\u000fH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b!\u0010\u001fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\""}, d2 = {"Lcom/deepworktracker/data/repository/SessionRepositoryImpl;", "Lcom/deepworktracker/domain/repository/SessionRepository;", "sessionDao", "Lcom/deepworktracker/data/database/dao/SessionDao;", "mapper", "Lcom/deepworktracker/data/mapper/SessionMapper;", "(Lcom/deepworktracker/data/database/dao/SessionDao;Lcom/deepworktracker/data/mapper/SessionMapper;)V", "deleteSession", "Lkotlin/Result;", "", "id", "", "deleteSession-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getActiveSession", "Lcom/deepworktracker/domain/model/FocusSession;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getRecentGoal", "", "getSessionById", "getSessionsByDate", "Lkotlinx/coroutines/flow/Flow;", "date", "Lkotlinx/datetime/LocalDate;", "getSessionsByDateRange", "startDate", "endDate", "observeActiveSession", "saveSession", "session", "saveSession-gIAlu-s", "(Lcom/deepworktracker/domain/model/FocusSession;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateSession", "updateSession-gIAlu-s", "data_debug"})
public final class SessionRepositoryImpl implements com.deepworktracker.domain.repository.SessionRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.data.database.dao.SessionDao sessionDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.data.mapper.SessionMapper mapper = null;
    
    @javax.inject.Inject()
    public SessionRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.dao.SessionDao sessionDao, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.mapper.SessionMapper mapper) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getActiveSession(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.deepworktracker.domain.model.FocusSession> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.deepworktracker.domain.model.FocusSession> observeActiveSession() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getSessionById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.deepworktracker.domain.model.FocusSession> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.deepworktracker.domain.model.FocusSession>> getSessionsByDate(@org.jetbrains.annotations.NotNull()
    kotlinx.datetime.LocalDate date) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.deepworktracker.domain.model.FocusSession>> getSessionsByDateRange(@org.jetbrains.annotations.NotNull()
    kotlinx.datetime.LocalDate startDate, @org.jetbrains.annotations.NotNull()
    kotlinx.datetime.LocalDate endDate) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getRecentGoal(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion) {
        return null;
    }
}