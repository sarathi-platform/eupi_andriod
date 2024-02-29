package com.nudge.syncmanager.di

import com.nudge.core.Core
import com.nudge.syncmanager.FirebaseRepository
import com.nudge.syncmanager.FirebaseRepositoryImpl
import com.nudge.syncmanager.SyncApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SyncManagerModule {
    @Singleton
    @Provides
    fun provideSyncApi(retrofit: Retrofit): SyncApiService {
        return retrofit.create(SyncApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCore(): Core {
        return Core()
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(core: Core): FirebaseRepository {
        return FirebaseRepositoryImpl(core = core)
    }
}