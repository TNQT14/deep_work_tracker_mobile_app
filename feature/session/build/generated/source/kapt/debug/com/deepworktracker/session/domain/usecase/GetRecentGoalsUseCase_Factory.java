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
public final class GetRecentGoalsUseCase_Factory implements Factory<GetRecentGoalsUseCase> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  public GetRecentGoalsUseCase_Factory(Provider<SessionRepository> sessionRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public GetRecentGoalsUseCase get() {
    return newInstance(sessionRepositoryProvider.get());
  }

  public static GetRecentGoalsUseCase_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new GetRecentGoalsUseCase_Factory(sessionRepositoryProvider);
  }

  public static GetRecentGoalsUseCase newInstance(SessionRepository sessionRepository) {
    return new GetRecentGoalsUseCase(sessionRepository);
  }
}
