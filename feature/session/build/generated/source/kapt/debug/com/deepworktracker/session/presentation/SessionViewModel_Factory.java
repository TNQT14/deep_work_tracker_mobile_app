package com.deepworktracker.session.presentation;

import com.deepworktracker.session.domain.usecase.EndSessionUseCase;
import com.deepworktracker.session.domain.usecase.GetActiveSessionUseCase;
import com.deepworktracker.session.domain.usecase.GetRecentGoalsUseCase;
import com.deepworktracker.session.domain.usecase.StartSessionUseCase;
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
public final class SessionViewModel_Factory implements Factory<SessionViewModel> {
  private final Provider<StartSessionUseCase> startSessionUseCaseProvider;

  private final Provider<EndSessionUseCase> endSessionUseCaseProvider;

  private final Provider<GetActiveSessionUseCase> getActiveSessionUseCaseProvider;

  private final Provider<GetRecentGoalsUseCase> getRecentGoalsUseCaseProvider;

  public SessionViewModel_Factory(Provider<StartSessionUseCase> startSessionUseCaseProvider,
      Provider<EndSessionUseCase> endSessionUseCaseProvider,
      Provider<GetActiveSessionUseCase> getActiveSessionUseCaseProvider,
      Provider<GetRecentGoalsUseCase> getRecentGoalsUseCaseProvider) {
    this.startSessionUseCaseProvider = startSessionUseCaseProvider;
    this.endSessionUseCaseProvider = endSessionUseCaseProvider;
    this.getActiveSessionUseCaseProvider = getActiveSessionUseCaseProvider;
    this.getRecentGoalsUseCaseProvider = getRecentGoalsUseCaseProvider;
  }

  @Override
  public SessionViewModel get() {
    return newInstance(startSessionUseCaseProvider.get(), endSessionUseCaseProvider.get(), getActiveSessionUseCaseProvider.get(), getRecentGoalsUseCaseProvider.get());
  }

  public static SessionViewModel_Factory create(
      Provider<StartSessionUseCase> startSessionUseCaseProvider,
      Provider<EndSessionUseCase> endSessionUseCaseProvider,
      Provider<GetActiveSessionUseCase> getActiveSessionUseCaseProvider,
      Provider<GetRecentGoalsUseCase> getRecentGoalsUseCaseProvider) {
    return new SessionViewModel_Factory(startSessionUseCaseProvider, endSessionUseCaseProvider, getActiveSessionUseCaseProvider, getRecentGoalsUseCaseProvider);
  }

  public static SessionViewModel newInstance(StartSessionUseCase startSessionUseCase,
      EndSessionUseCase endSessionUseCase, GetActiveSessionUseCase getActiveSessionUseCase,
      GetRecentGoalsUseCase getRecentGoalsUseCase) {
    return new SessionViewModel(startSessionUseCase, endSessionUseCase, getActiveSessionUseCase, getRecentGoalsUseCase);
  }
}
