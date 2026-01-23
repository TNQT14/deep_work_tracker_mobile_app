package com.deepworktracker.data.di;

import com.deepworktracker.data.database.DeepWorkDatabase;
import com.deepworktracker.data.database.dao.InsightDao;
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
public final class DataModule_ProvideInsightDaoFactory implements Factory<InsightDao> {
  private final Provider<DeepWorkDatabase> databaseProvider;

  public DataModule_ProvideInsightDaoFactory(Provider<DeepWorkDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public InsightDao get() {
    return provideInsightDao(databaseProvider.get());
  }

  public static DataModule_ProvideInsightDaoFactory create(
      Provider<DeepWorkDatabase> databaseProvider) {
    return new DataModule_ProvideInsightDaoFactory(databaseProvider);
  }

  public static InsightDao provideInsightDao(DeepWorkDatabase database) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideInsightDao(database));
  }
}
