package com.patsurvey.nudge.di

import com.patsurvey.nudge.activities.ui.progress.VillageSelectionRepository
import com.patsurvey.nudge.activities.ui.splash.ConfigRepository
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
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
    fun provideConfigRepository(
        apiService: ApiService, languageListDao: LanguageListDao
    ): ConfigRepository {
        return ConfigRepository(apiService, languageListDao)
    }
}