package com.patsurvey.nudge.di

import com.patsurvey.nudge.network.interfaces.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

//    @Singleton
//    @Provides
//    fun provideOrdersRepository( apiService: ApiService
//    ): OrderRepositoryV2 {
//        return OrderRepositoryV2(apiService)
//    }
}