package com.nudge.incomeexpensemodule.di

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.incomeexpensemodule.ui.screens.dataTab.domain.useCase.DataTabUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchMoneyJournalUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchLivelihoodSaveEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSubjectIncomeExpenseSummaryUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSubjectLivelihoodEventHistoryUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchAssetJournalUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsWithLivelihoodMappingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchLivelihoodOptionNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodUseCase
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
        fetchDidiDetailsWithLivelihoodMappingUseCase: FetchDidiDetailsWithLivelihoodMappingUseCase,
        fetchSubjectIncomeExpenseSummaryUseCase: FetchSubjectIncomeExpenseSummaryUseCase,
        fetchSubjectLivelihoodEventHistoryUseCase: FetchSubjectLivelihoodEventHistoryUseCase,
        assetJournalUseCase: FetchAssetJournalUseCase,
        fetchLivelihoodOptionNetworkUseCase: FetchLivelihoodOptionNetworkUseCase,
        fetchLivelihoodSaveEventUseCase: FetchLivelihoodSaveEventUseCase,
        livelihoodUseCase: LivelihoodUseCase,
        moneyJournalUseCase: FetchMoneyJournalUseCase
    ): DataTabUseCase {
        return DataTabUseCase(
            coreSharedPrefs = coreSharedPrefs,
            fetchDidiDetailsFromDbUseCase = fetchDidiDetailsFromDbUseCase,
            fetchDidiDetailsWithLivelihoodMappingUseCase = fetchDidiDetailsWithLivelihoodMappingUseCase,
            fetchSubjectIncomeExpenseSummaryUseCase = fetchSubjectIncomeExpenseSummaryUseCase,
            fetchSubjectLivelihoodEventHistoryUseCase = fetchSubjectLivelihoodEventHistoryUseCase,
            fetchLivelihoodSaveEventUseCase = fetchLivelihoodSaveEventUseCase,
            assetJournalUseCase = assetJournalUseCase,
            fetchLivelihoodOptionNetworkUseCase = fetchLivelihoodOptionNetworkUseCase,
            livelihoodUseCase = livelihoodUseCase,
            moneyJournalUseCase = moneyJournalUseCase
        )
    }

}