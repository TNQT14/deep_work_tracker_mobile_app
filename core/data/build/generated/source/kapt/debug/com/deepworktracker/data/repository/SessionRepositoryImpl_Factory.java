package com.deepworktracker.data.repository;

import com.deepworktracker.data.database.dao.SessionDao;
import com.deepworktracker.data.mapper.SessionMapper;
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
public final class SessionRepositoryImpl_Factory implements Factory<SessionRepositoryImpl> {
  private final Provider<SessionDao> sessionDaoProvider;

  private final Provider<SessionMapper> mapperProvider;

  public SessionRepositoryImpl_Factory(Provider<SessionDao> sessionDaoProvider,
      Provider<SessionMapper> mapperProvider) {
    this.sessionDaoProvider = sessionDaoProvider;
    this.mapperProvider = mapperProvider;
  }

  @Override
  public SessionRepositoryImpl get() {
    return newInstance(sessionDaoProvider.get(), mapperProvider.get());
  }

  public static SessionRepositoryImpl_Factory create(Provider<SessionDao> sessionDaoProvider,
      Provider<SessionMapper> mapperProvider) {
    return new SessionRepositoryImpl_Factory(sessionDaoProvider, mapperProvider);
  }

  public static SessionRepositoryImpl newInstance(SessionDao sessionDao, SessionMapper mapper) {
    return new SessionRepositoryImpl(sessionDao, mapper);
  }
}
