package com.deepworktracker.dashboard.presentation;

import com.deepworktracker.dashboard.domain.usecase.GetAllSessionUseCase;
import com.deepworktracker.dashboard.domain.usecase.GetRecentSessionsUseCase;
import com.deepworktracker.dashboard.domain.usecase.GetTodayStatsUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<GetTodayStatsUseCase> getTodayStatsUseCaseProvider;

  private final Provider<GetRecentSessionsUseCase> getRecentSessionsUseCaseProvider;

  private final Provider<GetAllSessionUseCase> getAllSessionUseCaseProvider;

  public DashboardViewModel_Factory(Provider<GetTodayStatsUseCase> getTodayStatsUseCaseProvider,
      Provider<GetRecentSessionsUseCase> getRecentSessionsUseCaseProvider,
      Provider<GetAllSessionUseCase> getAllSessionUseCaseProvider) {
    this.getTodayStatsUseCaseProvider = getTodayStatsUseCaseProvider;
    this.getRecentSessionsUseCaseProvider = getRecentSessionsUseCaseProvider;
    this.getAllSessionUseCaseProvider = getAllSessionUseCaseProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(getTodayStatsUseCaseProvider.get(), getRecentSessionsUseCaseProvider.get(), getAllSessionUseCaseProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<GetTodayStatsUseCase> getTodayStatsUseCaseProvider,
      Provider<GetRecentSessionsUseCase> getRecentSessionsUseCaseProvider,
      Provider<GetAllSessionUseCase> getAllSessionUseCaseProvider) {
    return new DashboardViewModel_Factory(getTodayStatsUseCaseProvider, getRecentSessionsUseCaseProvider, getAllSessionUseCaseProvider);
  }

  public static DashboardViewModel newInstance(GetTodayStatsUseCase getTodayStatsUseCase,
      GetRecentSessionsUseCase getRecentSessionsUseCase,
      GetAllSessionUseCase getAllSessionUseCase) {
    return new DashboardViewModel(getTodayStatsUseCase, getRecentSessionsUseCase, getAllSessionUseCase);
  }
}
