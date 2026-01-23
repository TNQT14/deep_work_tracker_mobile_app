package com.deepworktracker.data.di;

import com.deepworktracker.data.database.dao.InsightDao;
import com.deepworktracker.data.mapper.InsightMapper;
import com.deepworktracker.domain.repository.InsightRepository;
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
public final class DataModule_ProvideInsightRepositoryFactory implements Factory<InsightRepository> {
  private final Provider<InsightDao> insightDaoProvider;

  private final Provider<InsightMapper> mapperProvider;

  public DataModule_ProvideInsightRepositoryFactory(Provider<InsightDao> insightDaoProvider,
      Provider<InsightMapper> mapperProvider) {
    this.insightDaoProvider = insightDaoProvider;
    this.mapperProvider = mapperProvider;
  }

  @Override
  public InsightRepository get() {
    return provideInsightRepository(insightDaoProvider.get(), mapperProvider.get());
  }

  public static DataModule_ProvideInsightRepositoryFactory create(
      Provider<InsightDao> insightDaoProvider, Provider<InsightMapper> mapperProvider) {
    return new DataModule_ProvideInsightRepositoryFactory(insightDaoProvider, mapperProvider);
  }

  public static InsightRepository provideInsightRepository(InsightDao insightDao,
      InsightMapper mapper) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideInsightRepository(insightDao, mapper));
  }
}
