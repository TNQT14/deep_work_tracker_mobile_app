package com.deepworktracker.data.di

import com.deepworktracker.data.remote.auth.AuthSessionRepository
import com.deepworktracker.data.remote.auth.AuthSessionRepositoryImpl
import com.deepworktracker.data.remote.auth.RefreshTokenCoordinator
import com.deepworktracker.data.remote.auth.RefreshTokenCoordinatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthSessionModule {

    @Provides
    @Singleton
    fun provideRefreshTokenCoordinator(
        impl: RefreshTokenCoordinatorImpl,
    ): RefreshTokenCoordinator = impl

    @Provides
    @Singleton
    fun provideAuthSessionRepository(
        impl: AuthSessionRepositoryImpl,
    ): AuthSessionRepository = impl
}
