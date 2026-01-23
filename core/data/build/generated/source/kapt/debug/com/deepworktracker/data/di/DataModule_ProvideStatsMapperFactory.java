package com.deepworktracker.data.di;

import com.deepworktracker.data.mapper.StatsMapper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class DataModule_ProvideStatsMapperFactory implements Factory<StatsMapper> {
  @Override
  public StatsMapper get() {
    return provideStatsMapper();
  }

  public static DataModule_ProvideStatsMapperFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static StatsMapper provideStatsMapper() {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideStatsMapper());
  }

  private static final class InstanceHolder {
    private static final DataModule_ProvideStatsMapperFactory INSTANCE = new DataModule_ProvideStatsMapperFactory();
  }
}
