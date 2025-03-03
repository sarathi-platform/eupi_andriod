package com.patsurvey.nudge.di

import com.nudge.core.usecase.language.LanguageConfigUseCase
import com.patsurvey.nudge.activities.ui.splash.ConfigRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.BpcScorePercentageDao
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
        apiService: ApiService,
        bpcScorePercentageDao: BpcScorePercentageDao,
        prefRepo: PrefRepo,
        languageConfigUseCase: LanguageConfigUseCase
    ): ConfigRepository {
        return ConfigRepository(
            apiService,
            bpcScorePercentageDao,
            prefRepo,
            languageConfigUseCase
        )
    }


}