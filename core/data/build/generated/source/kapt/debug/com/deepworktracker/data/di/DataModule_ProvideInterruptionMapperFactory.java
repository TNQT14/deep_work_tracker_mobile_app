package com.deepworktracker.data.di;

import com.deepworktracker.data.mapper.InterruptionMapper;
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
public final class DataModule_ProvideInterruptionMapperFactory implements Factory<InterruptionMapper> {
  @Override
  public InterruptionMapper get() {
    return provideInterruptionMapper();
  }

  public static DataModule_ProvideInterruptionMapperFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static InterruptionMapper provideInterruptionMapper() {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideInterruptionMapper());
  }

  private static final class InstanceHolder {
    private static final DataModule_ProvideInterruptionMapperFactory INSTANCE = new DataModule_ProvideInterruptionMapperFactory();
  }
}
