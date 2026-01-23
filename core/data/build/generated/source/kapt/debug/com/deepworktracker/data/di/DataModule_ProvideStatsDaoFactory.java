package com.deepworktracker.data.di;

import com.deepworktracker.data.database.DeepWorkDatabase;
import com.deepworktracker.data.database.dao.StatsDao;
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
public final class DataModule_ProvideStatsDaoFactory implements Factory<StatsDao> {
  private final Provider<DeepWorkDatabase> databaseProvider;

  public DataModule_ProvideStatsDaoFactory(Provider<DeepWorkDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public StatsDao get() {
    return provideStatsDao(databaseProvider.get());
  }

  public static DataModule_ProvideStatsDaoFactory create(
      Provider<DeepWorkDatabase> databaseProvider) {
    return new DataModule_ProvideStatsDaoFactory(databaseProvider);
  }

  public static StatsDao provideStatsDao(DeepWorkDatabase database) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideStatsDao(database));
  }
}
