package com.nathanfremont.data.di

import android.app.Application
import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.nathanfremont.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    @Named("applicationContext")
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    @Singleton
    @Named("baseApiUrl")
    fun provideBaseApiUrl(): String = BuildConfig.API_URL

    @Provides
    @Singleton
    @Named("apiKey")
    fun provideApiKey(): String = BuildConfig.API_KEY

    @Provides
    @Singleton
    fun provideBaseHttpClient(
    ): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(HTTP_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(HTTP_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val HTTP_READ_TIMEOUT_SECONDS: Long = 10
        private const val HTTP_WRITE_TIMEOUT_SECONDS: Long = 10
    }
}