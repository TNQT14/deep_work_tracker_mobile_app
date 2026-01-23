package com.deepworktracker.session.presentation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0006\u0010\u0014\u001a\u00020\u0015J\u0006\u0010\u0016\u001a\u00020\u0015J\u0006\u0010\u0017\u001a\u00020\u0015J\b\u0010\u0018\u001a\u00020\u0015H\u0002J\b\u0010\u0019\u001a\u00020\u0015H\u0014J\u000e\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u001cJ\b\u0010\u001d\u001a\u00020\u0015H\u0002J\b\u0010\u001e\u001a\u00020\u0015H\u0002R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006\u001f"}, d2 = {"Lcom/deepworktracker/session/presentation/SessionViewModel;", "Landroidx/lifecycle/ViewModel;", "startSessionUseCase", "Lcom/deepworktracker/session/domain/usecase/StartSessionUseCase;", "endSessionUseCase", "Lcom/deepworktracker/session/domain/usecase/EndSessionUseCase;", "getActiveSessionUseCase", "Lcom/deepworktracker/session/domain/usecase/GetActiveSessionUseCase;", "getRecentGoalsUseCase", "Lcom/deepworktracker/session/domain/usecase/GetRecentGoalsUseCase;", "(Lcom/deepworktracker/session/domain/usecase/StartSessionUseCase;Lcom/deepworktracker/session/domain/usecase/EndSessionUseCase;Lcom/deepworktracker/session/domain/usecase/GetActiveSessionUseCase;Lcom/deepworktracker/session/domain/usecase/GetRecentGoalsUseCase;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/deepworktracker/session/presentation/SessionUiState;", "timerJob", "Lkotlinx/coroutines/Job;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "clearError", "", "endSession", "loadRecentGoal", "observeActiveSession", "onCleared", "startSession", "goal", "", "startTimer", "stopTimer", "session_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SessionViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.session.domain.usecase.StartSessionUseCase startSessionUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.session.domain.usecase.EndSessionUseCase endSessionUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.session.domain.usecase.GetActiveSessionUseCase getActiveSessionUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.session.domain.usecase.GetRecentGoalsUseCase getRecentGoalsUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.deepworktracker.session.presentation.SessionUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.deepworktracker.session.presentation.SessionUiState> uiState = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job timerJob;
    
    @javax.inject.Inject()
    public SessionViewModel(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.session.domain.usecase.StartSessionUseCase startSessionUseCase, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.session.domain.usecase.EndSessionUseCase endSessionUseCase, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.session.domain.usecase.GetActiveSessionUseCase getActiveSessionUseCase, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.session.domain.usecase.GetRecentGoalsUseCase getRecentGoalsUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.deepworktracker.session.presentation.SessionUiState> getUiState() {
        return null;
    }
    
    public final void startSession(@org.jetbrains.annotations.NotNull()
    java.lang.String goal) {
    }
    
    public final void loadRecentGoal() {
    }
    
    public final void endSession() {
    }
    
    private final void observeActiveSession() {
    }
    
    private final void startTimer() {
    }
    
    private final void stopTimer() {
    }
    
    public final void clearError() {
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
}