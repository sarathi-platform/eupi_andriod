package com.sarathi.missionactivitytask.di

import com.sarathi.missionactivitytask.network.MatApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MatModule {
    @Singleton
    @Provides
    fun provideMatApi(retrofit: Retrofit): MatApiService {
        return retrofit.create(MatApiService::class.java)
    }
}