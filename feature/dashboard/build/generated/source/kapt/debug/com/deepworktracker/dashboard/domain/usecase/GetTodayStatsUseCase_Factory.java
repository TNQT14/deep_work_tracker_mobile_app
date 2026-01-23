package com.deepworktracker.dashboard.domain.usecase;

import com.deepworktracker.domain.repository.SessionRepository;
import com.deepworktracker.domain.repository.StatsRepository;
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
public final class GetTodayStatsUseCase_Factory implements Factory<GetTodayStatsUseCase> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<StatsRepository> statsRepositoryProvider;

  public GetTodayStatsUseCase_Factory(Provider<SessionRepository> sessionRepositoryProvider,
      Provider<StatsRepository> statsRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.statsRepositoryProvider = statsRepositoryProvider;
  }

  @Override
  public GetTodayStatsUseCase get() {
    return newInstance(sessionRepositoryProvider.get(), statsRepositoryProvider.get());
  }

  public static GetTodayStatsUseCase_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<StatsRepository> statsRepositoryProvider) {
    return new GetTodayStatsUseCase_Factory(sessionRepositoryProvider, statsRepositoryProvider);
  }

  public static GetTodayStatsUseCase newInstance(SessionRepository sessionRepository,
      StatsRepository statsRepository) {
    return new GetTodayStatsUseCase(sessionRepository, statsRepository);
  }
}
