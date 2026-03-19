package com.deepworktracker.data.di

import android.content.Context
import androidx.room.Room
import com.deepworktracker.data.database.DeepWorkDatabase
import com.deepworktracker.data.database.dao.InterruptionDao
import com.deepworktracker.data.database.dao.SessionDao
import com.deepworktracker.data.database.dao.StatsDao
import com.deepworktracker.data.database.dao.InsightDao
import com.deepworktracker.data.mapper.InterruptionMapper
import com.deepworktracker.data.mapper.SessionMapper
import com.deepworktracker.data.mapper.StatsMapper
import com.deepworktracker.data.mapper.InsightMapper
import com.deepworktracker.data.repository.InterruptionRepositoryImpl
import com.deepworktracker.data.repository.SessionRepositoryImpl
import com.deepworktracker.data.repository.StatsRepositoryImpl
import com.deepworktracker.data.repository.InsightRepositoryImpl
import com.deepworktracker.domain.repository.InterruptionRepository
import com.deepworktracker.domain.repository.SessionRepository
import com.deepworktracker.domain.repository.StatsRepository
import com.deepworktracker.domain.repository.InsightRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DeepWorkDatabase {
        return Room.databaseBuilder(
            context,
            DeepWorkDatabase::class.java,
            DeepWorkDatabase.DATABASE_NAME
        )
            .addMigrations(DeepWorkDatabase.MIGRATION_1_2)
            .build()
    }
    
    @Provides
    fun provideSessionDao(database: DeepWorkDatabase): SessionDao {
        return database.sessionDao()
    }
    
    @Provides
    fun provideInterruptionDao(database: DeepWorkDatabase): InterruptionDao {
        return database.interruptionDao()
    }
    
    @Provides
    fun provideStatsDao(database: DeepWorkDatabase): StatsDao {
        return database.statsDao()
    }
    
    @Provides
    fun provideInsightDao(database: DeepWorkDatabase): InsightDao {
        return database.insightDao()
    }

    @Provides
    fun provideCategoryRuleDao(database: DeepWorkDatabase): com.deepworktracker.data.database.dao.CategoryRuleDao {
        return database.categoryRuleDao()
    }
    
    // Mappers
    @Provides
    fun provideSessionMapper(): SessionMapper = SessionMapper()
    
    @Provides
    fun provideInterruptionMapper(): InterruptionMapper = InterruptionMapper()
    
    @Provides
    fun provideStatsMapper(): StatsMapper = StatsMapper()
    
    @Provides
    fun provideInsightMapper(): InsightMapper = InsightMapper()

    @Provides
    fun provideCategoryRuleMapper(): com.deepworktracker.data.mapper.CategoryRuleMapper =
        com.deepworktracker.data.mapper.CategoryRuleMapper()
    
    // Repository implementations
    @Provides
    @Singleton
    fun provideSessionRepository(
        sessionDao: SessionDao,
        mapper: SessionMapper
    ): SessionRepository {
        return SessionRepositoryImpl(sessionDao, mapper)
    }
    
    @Provides
    @Singleton
    fun provideInterruptionRepository(
        interruptionDao: InterruptionDao,
        mapper: InterruptionMapper
    ): InterruptionRepository {
        return InterruptionRepositoryImpl(interruptionDao, mapper)
    }
    
    @Provides
    @Singleton
    fun provideStatsRepository(
        statsDao: StatsDao,
        sessionDao: SessionDao,
        statsMapper: StatsMapper,
        sessionMapper: SessionMapper
    ): StatsRepository {
        return StatsRepositoryImpl(statsDao, sessionDao, statsMapper, sessionMapper)
    }
    
    @Provides
    @Singleton
    fun provideInsightRepository(
        insightDao: InsightDao,
        mapper: InsightMapper
    ): InsightRepository {
        return InsightRepositoryImpl(insightDao, mapper)
    }

    @Provides
    @Singleton
    fun provideCategoryRuleRepository(
        dao: com.deepworktracker.data.database.dao.CategoryRuleDao,
        mapper: com.deepworktracker.data.mapper.CategoryRuleMapper
    ): com.deepworktracker.domain.repository.CategoryRuleRepository {
        return com.deepworktracker.data.repository.CategoryRuleRepositoryImpl(dao, mapper)
    }
}
