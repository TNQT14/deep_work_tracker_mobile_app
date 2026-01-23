package com.deepworktracker.dashboard.domain.usecase;

import com.deepworktracker.domain.repository.SessionRepository;
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
public final class GetRecentSessionsUseCase_Factory implements Factory<GetRecentSessionsUseCase> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  public GetRecentSessionsUseCase_Factory(Provider<SessionRepository> sessionRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public GetRecentSessionsUseCase get() {
    return newInstance(sessionRepositoryProvider.get());
  }

  public static GetRecentSessionsUseCase_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new GetRecentSessionsUseCase_Factory(sessionRepositoryProvider);
  }

  public static GetRecentSessionsUseCase newInstance(SessionRepository sessionRepository) {
    return new GetRecentSessionsUseCase(sessionRepository);
  }
}
