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
public final class GetActiveSessionUseCase_Factory implements Factory<GetActiveSessionUseCase> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  public GetActiveSessionUseCase_Factory(Provider<SessionRepository> sessionRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public GetActiveSessionUseCase get() {
    return newInstance(sessionRepositoryProvider.get());
  }

  public static GetActiveSessionUseCase_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new GetActiveSessionUseCase_Factory(sessionRepositoryProvider);
  }

  public static GetActiveSessionUseCase newInstance(SessionRepository sessionRepository) {
    return new GetActiveSessionUseCase(sessionRepository);
  }
}
