package com.tothenew.android_starter_project.di

import com.tothenew.android_starter_project.network.interfaces.ApiService
import com.tothenew.android_starter_project.repository.OrderRepositoryV2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideOrdersRepository( apiService: ApiService
    ): OrderRepositoryV2 {
        return OrderRepositoryV2(apiService)
    }
}