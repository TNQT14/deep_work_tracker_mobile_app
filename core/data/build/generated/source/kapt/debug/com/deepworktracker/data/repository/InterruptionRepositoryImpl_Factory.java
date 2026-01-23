package com.deepworktracker.data.repository;

import com.deepworktracker.data.database.dao.InterruptionDao;
import com.deepworktracker.data.mapper.InterruptionMapper;
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
public final class InterruptionRepositoryImpl_Factory implements Factory<InterruptionRepositoryImpl> {
  private final Provider<InterruptionDao> interruptionDaoProvider;

  private final Provider<InterruptionMapper> mapperProvider;

  public InterruptionRepositoryImpl_Factory(Provider<InterruptionDao> interruptionDaoProvider,
      Provider<InterruptionMapper> mapperProvider) {
    this.interruptionDaoProvider = interruptionDaoProvider;
    this.mapperProvider = mapperProvider;
  }

  @Override
  public InterruptionRepositoryImpl get() {
    return newInstance(interruptionDaoProvider.get(), mapperProvider.get());
  }

  public static InterruptionRepositoryImpl_Factory create(
      Provider<InterruptionDao> interruptionDaoProvider,
      Provider<InterruptionMapper> mapperProvider) {
    return new InterruptionRepositoryImpl_Factory(interruptionDaoProvider, mapperProvider);
  }

  public static InterruptionRepositoryImpl newInstance(InterruptionDao interruptionDao,
      InterruptionMapper mapper) {
    return new InterruptionRepositoryImpl(interruptionDao, mapper);
  }
}
