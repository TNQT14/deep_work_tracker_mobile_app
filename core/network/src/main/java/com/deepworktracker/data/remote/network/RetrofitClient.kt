package com.deepworktracker.data.remote.network

import android.content.Context
import com.deepworktracker.data.remote.interceptor.AuthInterceptor
import com.deepworktracker.data.remote.interceptor.RetryInterceptor
import com.deepworktracker.data.remote.interceptor.TokenAuthenticator
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val CACHE_SIZE_BYTES = 10L * 1024L * 1024L

    fun createCache(context: Context): Cache {
        val directory = File(context.cacheDir, "http_cache")
        return Cache(directory, CACHE_SIZE_BYTES)
    }

    fun createGson(): Gson = GsonBuilder().serializeNulls().create()

    fun createLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    fun createMainOkHttpClient(
        cache: Cache,
        authInterceptor: AuthInterceptor,
        retryInterceptor: RetryInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(retryInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    fun createRefreshOkHttpClient(
        cache: Cache,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .authenticator(Authenticator.NONE)
            .build()
    }

    fun createRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: String,
        gson: Gson,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}
