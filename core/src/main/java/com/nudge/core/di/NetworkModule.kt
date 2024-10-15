package com.nudge.core.di

import com.nudge.core.apiService.CoreApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideDataLoadingApiService(retrofit: Retrofit): CoreApiService {
        return retrofit.create(CoreApiService::class.java)
    }
}