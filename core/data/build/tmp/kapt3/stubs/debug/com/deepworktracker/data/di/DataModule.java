package com.deepworktracker.data.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0004H\u0007J\b\u0010\n\u001a\u00020\u000bH\u0007J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u000bH\u0007J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\u0004H\u0007J\b\u0010\u0012\u001a\u00020\u0013H\u0007J\u0018\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u000f\u001a\u00020\u0013H\u0007J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\t\u001a\u00020\u0004H\u0007J\b\u0010\u0019\u001a\u00020\u001aH\u0007J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u000f\u001a\u00020\u001aH\u0007J\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\t\u001a\u00020\u0004H\u0007J\b\u0010 \u001a\u00020!H\u0007J(\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u001f2\u0006\u0010\u001d\u001a\u00020\u00182\u0006\u0010%\u001a\u00020!2\u0006\u0010&\u001a\u00020\u001aH\u0007\u00a8\u0006\'"}, d2 = {"Lcom/deepworktracker/data/di/DataModule;", "", "()V", "provideDatabase", "Lcom/deepworktracker/data/database/DeepWorkDatabase;", "context", "Landroid/content/Context;", "provideInsightDao", "Lcom/deepworktracker/data/database/dao/InsightDao;", "database", "provideInsightMapper", "Lcom/deepworktracker/data/mapper/InsightMapper;", "provideInsightRepository", "Lcom/deepworktracker/domain/repository/InsightRepository;", "insightDao", "mapper", "provideInterruptionDao", "Lcom/deepworktracker/data/database/dao/InterruptionDao;", "provideInterruptionMapper", "Lcom/deepworktracker/data/mapper/InterruptionMapper;", "provideInterruptionRepository", "Lcom/deepworktracker/domain/repository/InterruptionRepository;", "interruptionDao", "provideSessionDao", "Lcom/deepworktracker/data/database/dao/SessionDao;", "provideSessionMapper", "Lcom/deepworktracker/data/mapper/SessionMapper;", "provideSessionRepository", "Lcom/deepworktracker/domain/repository/SessionRepository;", "sessionDao", "provideStatsDao", "Lcom/deepworktracker/data/database/dao/StatsDao;", "provideStatsMapper", "Lcom/deepworktracker/data/mapper/StatsMapper;", "provideStatsRepository", "Lcom/deepworktracker/domain/repository/StatsRepository;", "statsDao", "statsMapper", "sessionMapper", "data_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class DataModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.deepworktracker.data.di.DataModule INSTANCE = null;
    
    private DataModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.data.database.DeepWorkDatabase provideDatabase(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.data.database.dao.SessionDao provideSessionDao(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.DeepWorkDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.data.database.dao.InterruptionDao provideInterruptionDao(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.DeepWorkDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.data.database.dao.StatsDao provideStatsDao(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.DeepWorkDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.data.database.dao.InsightDao provideInsightDao(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.DeepWorkDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.data.mapper.SessionMapper provideSessionMapper() {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.data.mapper.InterruptionMapper provideInterruptionMapper() {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.data.mapper.StatsMapper provideStatsMapper() {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.data.mapper.InsightMapper provideInsightMapper() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.domain.repository.SessionRepository provideSessionRepository(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.dao.SessionDao sessionDao, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.mapper.SessionMapper mapper) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.domain.repository.InterruptionRepository provideInterruptionRepository(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.dao.InterruptionDao interruptionDao, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.mapper.InterruptionMapper mapper) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.domain.repository.StatsRepository provideStatsRepository(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.dao.StatsDao statsDao, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.dao.SessionDao sessionDao, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.mapper.StatsMapper statsMapper, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.mapper.SessionMapper sessionMapper) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.deepworktracker.domain.repository.InsightRepository provideInsightRepository(@org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.database.dao.InsightDao insightDao, @org.jetbrains.annotations.NotNull()
    com.deepworktracker.data.mapper.InsightMapper mapper) {
        return null;
    }
}