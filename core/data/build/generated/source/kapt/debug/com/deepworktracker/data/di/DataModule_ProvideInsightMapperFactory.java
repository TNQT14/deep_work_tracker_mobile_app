package com.deepworktracker.data.di;

import com.deepworktracker.data.mapper.InsightMapper;
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
public final class DataModule_ProvideInsightMapperFactory implements Factory<InsightMapper> {
  @Override
  public InsightMapper get() {
    return provideInsightMapper();
  }

  public static DataModule_ProvideInsightMapperFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static InsightMapper provideInsightMapper() {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideInsightMapper());
  }

  private static final class InstanceHolder {
    private static final DataModule_ProvideInsightMapperFactory INSTANCE = new DataModule_ProvideInsightMapperFactory();
  }
}
