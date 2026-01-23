package com.deepworktracker.data.di;

import com.deepworktracker.data.database.DeepWorkDatabase;
import com.deepworktracker.data.database.dao.InterruptionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DataModule_ProvideInterruptionDaoFactory implements Factory<InterruptionDao> {
  private final Provider<DeepWorkDatabase> databaseProvider;

  public DataModule_ProvideInterruptionDaoFactory(Provider<DeepWorkDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public InterruptionDao get() {
    return provideInterruptionDao(databaseProvider.get());
  }

  public static DataModule_ProvideInterruptionDaoFactory create(
      Provider<DeepWorkDatabase> databaseProvider) {
    return new DataModule_ProvideInterruptionDaoFactory(databaseProvider);
  }

  public static InterruptionDao provideInterruptionDao(DeepWorkDatabase database) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideInterruptionDao(database));
  }
}
