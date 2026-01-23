package com.deepworktracker.data.di;

import com.deepworktracker.data.database.dao.InterruptionDao;
import com.deepworktracker.data.mapper.InterruptionMapper;
import com.deepworktracker.domain.repository.InterruptionRepository;
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
public final class DataModule_ProvideInterruptionRepositoryFactory implements Factory<InterruptionRepository> {
  private final Provider<InterruptionDao> interruptionDaoProvider;

  private final Provider<InterruptionMapper> mapperProvider;

  public DataModule_ProvideInterruptionRepositoryFactory(
      Provider<InterruptionDao> interruptionDaoProvider,
      Provider<InterruptionMapper> mapperProvider) {
    this.interruptionDaoProvider = interruptionDaoProvider;
    this.mapperProvider = mapperProvider;
  }

  @Override
  public InterruptionRepository get() {
    return provideInterruptionRepository(interruptionDaoProvider.get(), mapperProvider.get());
  }

  public static DataModule_ProvideInterruptionRepositoryFactory create(
      Provider<InterruptionDao> interruptionDaoProvider,
      Provider<InterruptionMapper> mapperProvider) {
    return new DataModule_ProvideInterruptionRepositoryFactory(interruptionDaoProvider, mapperProvider);
  }

  public static InterruptionRepository provideInterruptionRepository(
      InterruptionDao interruptionDao, InterruptionMapper mapper) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideInterruptionRepository(interruptionDao, mapper));
  }
}
