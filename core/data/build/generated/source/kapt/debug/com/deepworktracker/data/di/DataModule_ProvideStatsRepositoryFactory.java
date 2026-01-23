package com.deepworktracker.data.di;

import com.deepworktracker.data.database.dao.SessionDao;
import com.deepworktracker.data.database.dao.StatsDao;
import com.deepworktracker.data.mapper.SessionMapper;
import com.deepworktracker.data.mapper.StatsMapper;
import com.deepworktracker.domain.repository.StatsRepository;
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
public final class DataModule_ProvideStatsRepositoryFactory implements Factory<StatsRepository> {
  private final Provider<StatsDao> statsDaoProvider;

  private final Provider<SessionDao> sessionDaoProvider;

  private final Provider<StatsMapper> statsMapperProvider;

  private final Provider<SessionMapper> sessionMapperProvider;

  public DataModule_ProvideStatsRepositoryFactory(Provider<StatsDao> statsDaoProvider,
      Provider<SessionDao> sessionDaoProvider, Provider<StatsMapper> statsMapperProvider,
      Provider<SessionMapper> sessionMapperProvider) {
    this.statsDaoProvider = statsDaoProvider;
    this.sessionDaoProvider = sessionDaoProvider;
    this.statsMapperProvider = statsMapperProvider;
    this.sessionMapperProvider = sessionMapperProvider;
  }

  @Override
  public StatsRepository get() {
    return provideStatsRepository(statsDaoProvider.get(), sessionDaoProvider.get(), statsMapperProvider.get(), sessionMapperProvider.get());
  }

  public static DataModule_ProvideStatsRepositoryFactory create(Provider<StatsDao> statsDaoProvider,
      Provider<SessionDao> sessionDaoProvider, Provider<StatsMapper> statsMapperProvider,
      Provider<SessionMapper> sessionMapperProvider) {
    return new DataModule_ProvideStatsRepositoryFactory(statsDaoProvider, sessionDaoProvider, statsMapperProvider, sessionMapperProvider);
  }

  public static StatsRepository provideStatsRepository(StatsDao statsDao, SessionDao sessionDao,
      StatsMapper statsMapper, SessionMapper sessionMapper) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideStatsRepository(statsDao, sessionDao, statsMapper, sessionMapper));
  }
}
