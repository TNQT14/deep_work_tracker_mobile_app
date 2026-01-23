package com.deepworktracker.dashboard.presentation;

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

  public DashboardViewModel_Factory(Provider<GetTodayStatsUseCase> getTodayStatsUseCaseProvider,
      Provider<GetRecentSessionsUseCase> getRecentSessionsUseCaseProvider) {
    this.getTodayStatsUseCaseProvider = getTodayStatsUseCaseProvider;
    this.getRecentSessionsUseCaseProvider = getRecentSessionsUseCaseProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(getTodayStatsUseCaseProvider.get(), getRecentSessionsUseCaseProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<GetTodayStatsUseCase> getTodayStatsUseCaseProvider,
      Provider<GetRecentSessionsUseCase> getRecentSessionsUseCaseProvider) {
    return new DashboardViewModel_Factory(getTodayStatsUseCaseProvider, getRecentSessionsUseCaseProvider);
  }

  public static DashboardViewModel newInstance(GetTodayStatsUseCase getTodayStatsUseCase,
      GetRecentSessionsUseCase getRecentSessionsUseCase) {
    return new DashboardViewModel(getTodayStatsUseCase, getRecentSessionsUseCase);
  }
}
