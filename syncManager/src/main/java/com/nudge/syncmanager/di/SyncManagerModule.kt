package com.nudge.syncmanager.di

import android.content.Context
import androidx.work.WorkManager
import com.nudge.core.Core
import com.nudge.syncmanager.firebase.FirebaseRepository
import com.nudge.syncmanager.firebase.FirebaseRepositoryImpl
import com.nudge.syncmanager.imageupload.BlobImageUploader
import com.nudge.syncmanager.imageupload.ImageUploader
import com.nudge.syncmanager.network.SyncApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context):WorkManager{
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideImageUploader(): ImageUploader {
        return BlobImageUploader()
    }

}