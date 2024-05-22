package com.sarathi.missionactivitytask.di

import com.sarathi.missionactivitytask.domain.repository.GetMissionRepositoryImpl
import com.sarathi.missionactivitytask.domain.usecases.GetMissionsUseCase
import com.sarathi.missionactivitytask.network.MatApiService
import com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.repository.GetActivityRepositoryImpl
import com.sarathi.missionactivitytask.ui.grant_activity_screen.domain.usecase.GetActivityUseCase
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

    @Singleton
    @Provides
    fun provideMissionUseCase(missionRepository: GetMissionRepositoryImpl): GetMissionsUseCase {
        return GetMissionsUseCase(missionRepository)
    }

    @Singleton
    @Provides
    fun provideActivityUseCase(activityRepositoryImpl: GetActivityRepositoryImpl): GetActivityUseCase {
        return GetActivityUseCase(activityRepositoryImpl)
    }


}