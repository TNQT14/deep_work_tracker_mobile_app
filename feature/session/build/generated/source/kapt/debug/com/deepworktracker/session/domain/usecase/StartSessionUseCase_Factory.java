package com.deepworktracker.session.domain.usecase;

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
public final class StartSessionUseCase_Factory implements Factory<StartSessionUseCase> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  public StartSessionUseCase_Factory(Provider<SessionRepository> sessionRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public StartSessionUseCase get() {
    return newInstance(sessionRepositoryProvider.get());
  }

  public static StartSessionUseCase_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new StartSessionUseCase_Factory(sessionRepositoryProvider);
  }

  public static StartSessionUseCase newInstance(SessionRepository sessionRepository) {
    return new StartSessionUseCase(sessionRepository);
  }
}
