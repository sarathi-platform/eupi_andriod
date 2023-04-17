package com.patsurvey.nudge.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.network.BaseNetworkConstants
import com.patsurvey.nudge.network.ErrorInterceptor
import com.patsurvey.nudge.network.interfaces.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * created by anil on 18/05/22
 */

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

  @Provides
  @Singleton
  fun provideApiService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
  }

  /**
   * Creates Retrofit object
   *
   * @return
   */
  @Singleton
  @Provides
  fun provideRetrofit(gson: Gson): Retrofit {
    val baseUrl = BaseNetworkConstants.DOMAIN
    val builder = Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(provideOkHttpClient())
    builder.addConverterFactory(GsonConverterFactory.create(gson))
    return builder.build()
  }

  @Singleton
  @Provides
  fun provideGson(): Gson {
    return GsonBuilder()
      .setLenient()
      .create()
  }

  @Singleton
  @Provides
  fun provideOkHttpClient(): OkHttpClient {
    val httpClientBuilder =
      OkHttpClient
        .Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .addInterceptor(ErrorInterceptor())
        .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))

    if (BuildConfig.DEBUG) {
      val logging = HttpLoggingInterceptor()
      logging.level = HttpLoggingInterceptor.Level.BODY
      httpClientBuilder.addInterceptor(logging)
    }
    return httpClientBuilder.build()
  }

}