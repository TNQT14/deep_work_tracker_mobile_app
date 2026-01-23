package com.deepworktracker.data.di;

import com.deepworktracker.data.mapper.SessionMapper;
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
public final class DataModule_ProvideSessionMapperFactory implements Factory<SessionMapper> {
  @Override
  public SessionMapper get() {
    return provideSessionMapper();
  }

  public static DataModule_ProvideSessionMapperFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SessionMapper provideSessionMapper() {
    return Preconditions.checkNotNullFromProvides(DataModule.INSTANCE.provideSessionMapper());
  }

  private static final class InstanceHolder {
    private static final DataModule_ProvideSessionMapperFactory INSTANCE = new DataModule_ProvideSessionMapperFactory();
  }
}
