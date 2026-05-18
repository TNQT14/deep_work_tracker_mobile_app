package com.deepworktracker.data.di

import android.content.Context
import com.deepworktracker.data.remote.api.AuthApi
import com.deepworktracker.data.remote.api.TokenRefreshApi
import com.deepworktracker.data.remote.interceptor.AuthInterceptor
import com.deepworktracker.data.remote.interceptor.RetryInterceptor
import com.deepworktracker.data.remote.interceptor.TokenAuthenticator
import com.deepworktracker.data.remote.network.RetrofitClient
import com.deepworktracker.data.remote.token.InMemoryTokenStore
import com.deepworktracker.data.remote.token.TokenStore
import com.deepworktracker.network.BuildConfig
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = RetrofitClient.createGson()

    @Provides
    @Singleton
    fun provideTokenStore(impl: InMemoryTokenStore): TokenStore = impl

    @Provides
    @Singleton
    fun provideHttpCache(
        @ApplicationContext context: Context,
    ): Cache = RetrofitClient.createCache(context)

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return RetrofitClient.createLoggingInterceptor(BuildConfig.DEBUG)
    }

    @Provides
    @Singleton
    fun provideRetryInterceptor(): RetryInterceptor = RetryInterceptor()

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenStore: TokenStore,
    ): AuthInterceptor = AuthInterceptor(tokenStore)

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenStore: TokenStore,
        tokenRefreshApi: TokenRefreshApi,
    ): TokenAuthenticator = TokenAuthenticator(tokenStore, tokenRefreshApi)

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshOkHttpClient(
        cache: Cache,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return RetrofitClient.createRefreshOkHttpClient(cache, loggingInterceptor)
    }

    @Provides
    @Singleton
    fun provideTokenRefreshApi(
        @Named("refresh") okHttpClient: OkHttpClient,
        gson: Gson,
        @BaseUrl baseUrl: String,
    ): TokenRefreshApi {
        return RetrofitClient.createRetrofit(okHttpClient, baseUrl, gson)
            .create(TokenRefreshApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        authInterceptor: AuthInterceptor,
        retryInterceptor: RetryInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient {
        return RetrofitClient.createMainOkHttpClient(
            cache = cache,
            authInterceptor = authInterceptor,
            retryInterceptor = retryInterceptor,
            loggingInterceptor = loggingInterceptor,
            tokenAuthenticator = tokenAuthenticator,
        )
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
        @BaseUrl baseUrl: String,
    ): Retrofit = RetrofitClient.createRetrofit(okHttpClient, baseUrl, gson)

    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit,
    ): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.BASE_URL
}
