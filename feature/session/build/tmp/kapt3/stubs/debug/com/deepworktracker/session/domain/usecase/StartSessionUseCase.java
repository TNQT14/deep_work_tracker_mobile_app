package com.deepworktracker.session.domain.usecase;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0086B\u00a2\u0006\u0002\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/deepworktracker/session/domain/usecase/StartSessionUseCase;", "", "sessionRepository", "Lcom/deepworktracker/domain/repository/SessionRepository;", "(Lcom/deepworktracker/domain/repository/SessionRepository;)V", "invoke", "Lcom/deepworktracker/common/result/Result;", "Lcom/deepworktracker/domain/model/FocusSession;", "goal", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "session_debug"})
public final class StartSessionUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.domain.repository.SessionRepository sessionRepository = null;
    
    @javax.inject.Inject()
    public StartSessionUseCase(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.domain.repository.SessionRepository sessionRepository) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object invoke(@org.jetbrains.annotations.NotNull()
    java.lang.String goal, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.deepworktracker.common.result.Result<com.deepworktracker.domain.model.FocusSession>> $completion) {
        return null;
    }
}