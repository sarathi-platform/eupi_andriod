package com.nudge.core.di

import com.nudge.core.apiService.CoreApiService
import com.nudge.core.data.repository.AppConfigDatabaseRepository
import com.nudge.core.data.repository.AppConfigDatabaseRepositoryImpl
import com.nudge.core.data.repository.AppConfigNetworkRepository
import com.nudge.core.data.repository.AppConfigNetworkRepositoryImpl
import com.nudge.core.data.repository.SyncMigrationRepository
import com.nudge.core.data.repository.SyncMigrationRepositoryImpl
import com.nudge.core.database.dao.ApiConfigDao
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.preference.CorePrefRepo
import com.nudge.core.preference.CoreSharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideFetchAppConfigFromNetwork(
        coreApiService: CoreApiService,
        coreSharedPrefs: CoreSharedPrefs
    ): AppConfigNetworkRepository {
        return AppConfigNetworkRepositoryImpl(
            coreApiService = coreApiService,
            coreSharedPrefs = coreSharedPrefs
        )
    }

    @Singleton
    @Provides
    fun provideFetchAppConfigFromDatabase(
        appConfigDao: ApiConfigDao,
        coreSharedPrefs: CoreSharedPrefs
    ): AppConfigDatabaseRepository {
        return AppConfigDatabaseRepositoryImpl(
            appConfigDao = appConfigDao,
            coreSharedPrefs = coreSharedPrefs
        )
    }

    @Singleton
    @Provides
    fun provideSyncMigration(
        eventsDao: EventsDao,
        eventDependencyDao: EventDependencyDao,
        corePrefRepo: CorePrefRepo
    ): SyncMigrationRepository {
        return SyncMigrationRepositoryImpl(
            eventsDao = eventsDao,
            eventDependencyDao = eventDependencyDao,
            corePrefRepo = corePrefRepo
        )
    }

}