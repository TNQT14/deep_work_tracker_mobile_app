package com.deepworktracker.dashboard.presentation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\u000fJ\u0006\u0010\u0011\u001a\u00020\u000fR\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0012"}, d2 = {"Lcom/deepworktracker/dashboard/presentation/DashboardViewModel;", "Landroidx/lifecycle/ViewModel;", "getTodayStatsUseCase", "Lcom/deepworktracker/dashboard/domain/usecase/GetTodayStatsUseCase;", "getRecentSessionsUseCase", "Lcom/deepworktracker/dashboard/domain/usecase/GetRecentSessionsUseCase;", "(Lcom/deepworktracker/dashboard/domain/usecase/GetTodayStatsUseCase;Lcom/deepworktracker/dashboard/domain/usecase/GetRecentSessionsUseCase;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/deepworktracker/dashboard/presentation/DashboardUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "clearError", "", "loadDashboardData", "refresh", "dashboard_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class DashboardViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.dashboard.domain.usecase.GetTodayStatsUseCase getTodayStatsUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.deepworktracker.dashboard.domain.usecase.GetRecentSessionsUseCase getRecentSessionsUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.deepworktracker.dashboard.presentation.DashboardUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.deepworktracker.dashboard.presentation.DashboardUiState> uiState = null;
    
    @javax.inject.Inject()
    public DashboardViewModel(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.dashboard.domain.usecase.GetTodayStatsUseCase getTodayStatsUseCase, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.dashboard.domain.usecase.GetRecentSessionsUseCase getRecentSessionsUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.deepworktracker.dashboard.presentation.DashboardUiState> getUiState() {
        return null;
    }
    
    public final void loadDashboardData() {
    }
    
    public final void refresh() {
    }
    
    public final void clearError() {
    }
}