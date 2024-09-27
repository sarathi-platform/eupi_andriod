package com.nrlm.baselinesurvey.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    /*@Singleton
    @Provides
    fun provideConfigRepository(
        baseLineApiService: BaseLineApiService, languageListDao: LanguageListDao, bpcScorePercentageDao: BpcScorePercentageDao, prefRepo: PrefRepo
    ): ConfigRepository {
        return ConfigRepository(baseLineApiService, languageListDao,bpcScorePercentageDao,prefRepo)
    }*/
}