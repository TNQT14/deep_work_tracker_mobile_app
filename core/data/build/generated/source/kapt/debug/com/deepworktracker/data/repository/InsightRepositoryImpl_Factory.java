package com.deepworktracker.data.repository;

import com.deepworktracker.data.database.dao.InsightDao;
import com.deepworktracker.data.mapper.InsightMapper;
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
public final class InsightRepositoryImpl_Factory implements Factory<InsightRepositoryImpl> {
  private final Provider<InsightDao> insightDaoProvider;

  private final Provider<InsightMapper> mapperProvider;

  public InsightRepositoryImpl_Factory(Provider<InsightDao> insightDaoProvider,
      Provider<InsightMapper> mapperProvider) {
    this.insightDaoProvider = insightDaoProvider;
    this.mapperProvider = mapperProvider;
  }

  @Override
  public InsightRepositoryImpl get() {
    return newInstance(insightDaoProvider.get(), mapperProvider.get());
  }

  public static InsightRepositoryImpl_Factory create(Provider<InsightDao> insightDaoProvider,
      Provider<InsightMapper> mapperProvider) {
    return new InsightRepositoryImpl_Factory(insightDaoProvider, mapperProvider);
  }

  public static InsightRepositoryImpl newInstance(InsightDao insightDao, InsightMapper mapper) {
    return new InsightRepositoryImpl(insightDao, mapper);
  }
}
