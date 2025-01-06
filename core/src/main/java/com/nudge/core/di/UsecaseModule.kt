package com.nudge.core.di

import com.nudge.core.analytics.AnalyticsManager
import com.nudge.core.data.repository.AppConfigDatabaseRepositoryImpl
import com.nudge.core.data.repository.AppConfigNetworkRepositoryImpl
import com.nudge.core.data.repository.SyncMigrationRepository
import com.nudge.core.usecase.AnalyticsEventUseCase
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.nudge.core.usecase.SyncMigrationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class UsecaseModule {
    @Singleton
    @Provides
    fun provideFetchAppConfigFromNetwork(
        appConfigNetworkRepositoryImpl: AppConfigNetworkRepositoryImpl,
        appConfigDatabaseRepositoryImpl: AppConfigDatabaseRepositoryImpl
    ): FetchAppConfigFromNetworkUseCase {
        return FetchAppConfigFromNetworkUseCase(
            apiConfigNetworkRepository = appConfigNetworkRepositoryImpl,
            apiConfigDatabaseRepository = appConfigDatabaseRepositoryImpl
        )
    }

    @Singleton
    @Provides
    fun provideFetchAppConfigFromCacheOrDb(
        appConfigDatabaseRepositoryImpl: AppConfigDatabaseRepositoryImpl
    ): FetchAppConfigFromCacheOrDbUsecase {
        return FetchAppConfigFromCacheOrDbUsecase(
            apiConfigDatabaseRepository = appConfigDatabaseRepositoryImpl
        )
    }

    @Singleton
    @Provides
    fun provideSyncMigrationUseCase(
        repository: SyncMigrationRepository
    ): SyncMigrationUseCase {
        return SyncMigrationUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAnalyticsEventUseCase(
        analyticsManager: AnalyticsManager,
    ): AnalyticsEventUseCase {
        return AnalyticsEventUseCase(analyticsManager)
    }
}