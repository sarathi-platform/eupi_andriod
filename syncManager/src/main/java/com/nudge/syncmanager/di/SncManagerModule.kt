package com.nudge.syncmanager.di

import com.nudge.core.database.dao.EventsDao
import com.nudge.syncmanager.SyncApiRepository
import com.nudge.syncmanager.SyncApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SncManagerModule {
    @Singleton
    @Provides
    fun provideSyncApi(retrofit: Retrofit): SyncApiService {
        return retrofit.create(SyncApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideSyncRepository(syncApiService: SyncApiService,eventDao: EventsDao): SyncApiRepository {
        return SyncApiRepository(syncApiService,eventDao)
    }
}