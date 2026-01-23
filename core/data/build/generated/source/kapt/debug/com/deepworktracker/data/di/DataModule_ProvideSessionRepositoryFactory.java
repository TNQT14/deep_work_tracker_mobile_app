package com.deepworktracker.data.di;

import com.deepworktracker.data.database.dao.SessionDao;
import com.deepworktracker.data.mapper.SessionMapper;
import com.deepworktracker.domain.repository.SessionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DataModule_ProvideSessionRepositoryFactory implements Factory<SessionRepository> {
  private final Provider<SessionDao> sessionDaoProvider;

  private final Provider<SessionMapper> mapperProvider;

  public DataModule_ProvideSessionRepositoryFactory(Provider<SessionDao> sessionDaoProvider,
      Provider<SessionMapper> mapperProvider) {
    this.sessionDaoProvider = sessionDaoProvider;
    this.mapperProvider = mapperProvider;
  }

  @Override
  public SessionRepository get() {
    return provideSessionRepository(sessionDaoProvider.get(), mapperProvider.get());
  }

  public static DataModule_ProvideSessionRepositoryFactory create(
      Provider<SessionDao> sessionDaoProvider, Provider<SessionMapper> mapperProvider) {
    return new DataModule_ProvideSessionRepositoryFactory(sessionDaoProvider, mapperProvider);
  }

  public static SessionRepository provideSessionRepository(SessionDao sessionDao,
      SessionMapper mapper) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideSessionRepository(sessionDao, mapper));
  }
}
