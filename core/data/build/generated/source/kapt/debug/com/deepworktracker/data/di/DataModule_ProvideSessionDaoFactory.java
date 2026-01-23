package com.deepworktracker.data.di;

import com.deepworktracker.data.database.DeepWorkDatabase;
import com.deepworktracker.data.database.dao.SessionDao;
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
public final class DataModule_ProvideSessionDaoFactory implements Factory<SessionDao> {
  private final Provider<DeepWorkDatabase> databaseProvider;

  public DataModule_ProvideSessionDaoFactory(Provider<DeepWorkDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SessionDao get() {
    return provideSessionDao(databaseProvider.get());
  }

  public static DataModule_ProvideSessionDaoFactory create(
      Provider<DeepWorkDatabase> databaseProvider) {
    return new DataModule_ProvideSessionDaoFactory(databaseProvider);
  }

  public static SessionDao provideSessionDao(DeepWorkDatabase database) {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideSessionDao(database));
  }
}
