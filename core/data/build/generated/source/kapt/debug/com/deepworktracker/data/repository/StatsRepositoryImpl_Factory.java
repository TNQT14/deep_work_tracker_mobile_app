package com.deepworktracker.data.repository;

import com.deepworktracker.data.database.dao.SessionDao;
import com.deepworktracker.data.database.dao.StatsDao;
import com.deepworktracker.data.mapper.SessionMapper;
import com.deepworktracker.data.mapper.StatsMapper;
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
public final class StatsRepositoryImpl_Factory implements Factory<StatsRepositoryImpl> {
  private final Provider<StatsDao> statsDaoProvider;

  private final Provider<SessionDao> sessionDaoProvider;

  private final Provider<StatsMapper> statsMapperProvider;

  private final Provider<SessionMapper> sessionMapperProvider;

  public StatsRepositoryImpl_Factory(Provider<StatsDao> statsDaoProvider,
      Provider<SessionDao> sessionDaoProvider, Provider<StatsMapper> statsMapperProvider,
      Provider<SessionMapper> sessionMapperProvider) {
    this.statsDaoProvider = statsDaoProvider;
    this.sessionDaoProvider = sessionDaoProvider;
    this.statsMapperProvider = statsMapperProvider;
    this.sessionMapperProvider = sessionMapperProvider;
  }

  @Override
  public StatsRepositoryImpl get() {
    return newInstance(statsDaoProvider.get(), sessionDaoProvider.get(), statsMapperProvider.get(), sessionMapperProvider.get());
  }

  public static StatsRepositoryImpl_Factory create(Provider<StatsDao> statsDaoProvider,
      Provider<SessionDao> sessionDaoProvider, Provider<StatsMapper> statsMapperProvider,
      Provider<SessionMapper> sessionMapperProvider) {
    return new StatsRepositoryImpl_Factory(statsDaoProvider, sessionDaoProvider, statsMapperProvider, sessionMapperProvider);
  }

  public static StatsRepositoryImpl newInstance(StatsDao statsDao, SessionDao sessionDao,
      StatsMapper statsMapper, SessionMapper sessionMapper) {
    return new StatsRepositoryImpl(statsDao, sessionDao, statsMapper, sessionMapper);
  }
}
