package com.nudge.core.di.caste

import com.nudge.core.apiService.CoreApiService
import com.nudge.core.data.repository.caste.CasteConfigRepositoryImpl
import com.nudge.core.database.dao.CasteListDao
import com.nudge.core.preference.CoreSharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CasteConfigModule {
    @Singleton
    @Provides
    fun provideFetchAppConfigFromNetwork(
        coreApiService: CoreApiService,
        casteListDao: CasteListDao,
        coreSharedPrefs: CoreSharedPrefs
    ): CasteConfigRepositoryImpl {
        return CasteConfigRepositoryImpl(
            coreApiService = coreApiService,
            coreSharedPrefs = coreSharedPrefs,
            casteListDao = casteListDao
        )
    }
}