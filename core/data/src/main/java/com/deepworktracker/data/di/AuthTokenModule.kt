package com.deepworktracker.data.di

import com.deepworktracker.data.remote.token.HybridTokenStore
import com.deepworktracker.data.remote.token.TokenStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthTokenModule {
    @Binds
    @Singleton
    abstract fun bindTokenStore(impl: HybridTokenStore): TokenStore
}