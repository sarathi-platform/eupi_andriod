package com.nudge.incomeexpensemodule.di

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.incomeexpensemodule.ui.screens.dataTab.domain.useCase.DataTabUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsWithLivelihoodMappingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LivelihoodModule {

    @Provides
    @Singleton
    fun provideDataTabUseCase(
        coreSharedPrefs: CoreSharedPrefs,
        fetchDidiDetailsFromDbUseCase: FetchDidiDetailsFromDbUseCase,
        fetchDidiDetailsWithLivelihoodMappingUseCase: FetchDidiDetailsWithLivelihoodMappingUseCase
    ): DataTabUseCase {
        return DataTabUseCase(
            coreSharedPrefs = coreSharedPrefs,
            fetchDidiDetailsFromDbUseCase = fetchDidiDetailsFromDbUseCase,
            fetchDidiDetailsWithLivelihoodMappingUseCase = fetchDidiDetailsWithLivelihoodMappingUseCase
        )
    }

}