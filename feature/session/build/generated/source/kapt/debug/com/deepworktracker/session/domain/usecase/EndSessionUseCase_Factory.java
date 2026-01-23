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
public final class EndSessionUseCase_Factory implements Factory<EndSessionUseCase> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  public EndSessionUseCase_Factory(Provider<SessionRepository> sessionRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public EndSessionUseCase get() {
    return newInstance(sessionRepositoryProvider.get());
  }

  public static EndSessionUseCase_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new EndSessionUseCase_Factory(sessionRepositoryProvider);
  }

  public static EndSessionUseCase newInstance(SessionRepository sessionRepository) {
    return new EndSessionUseCase(sessionRepository);
  }
}
