package com.nudge.incomeexpensemodule.ui.screens.dataTab.domain.useCase

import com.nudge.core.BLANK_STRING
import com.nudge.core.CoreDispatchers
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchLivelihoodSaveEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSubjectIncomeExpenseSummaryUseCase
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchSubjectLivelihoodEventHistoryUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchAssetJournalUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchDidiDetailsWithLivelihoodMappingUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchLivelihoodOptionNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodUseCase
import kotlinx.coroutines.withContext
import javax.inject.Inject


class DataTabUseCase @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    val fetchDidiDetailsFromDbUseCase: FetchDidiDetailsFromDbUseCase,
    val fetchDidiDetailsWithLivelihoodMappingUseCase: FetchDidiDetailsWithLivelihoodMappingUseCase,
    val fetchSubjectIncomeExpenseSummaryUseCase: FetchSubjectIncomeExpenseSummaryUseCase,
    val fetchSubjectLivelihoodEventHistoryUseCase: FetchSubjectLivelihoodEventHistoryUseCase,
    val assetJournalUseCase: FetchAssetJournalUseCase,
    val fetchLivelihoodOptionNetworkUseCase: FetchLivelihoodOptionNetworkUseCase,
    val fetchLivelihoodSaveEventUseCase: FetchLivelihoodSaveEventUseCase,
    val livelihoodUseCase: LivelihoodUseCase,


    ) {

    suspend operator fun invoke(
        isRefresh: Boolean,
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
    ) {

        try {

            if (isRefresh || !coreSharedPrefs.isDataTabDataLoaded()) {
                fetchDidiDetailsFromDbUseCase.invoke()
                if (!isRefresh) {
                    fetchLivelihoodSaveEventUseCase.invoke()
                    fetchLivelihoodOptionNetworkUseCase.invoke()
                    assetJournalUseCase.invoke()
                }
                livelihoodUseCase.invoke()


                withContext(CoreDispatchers.mainDispatcher) {
                    onComplete(true, BLANK_STRING)
                }
            } else {
                withContext(CoreDispatchers.mainDispatcher) {
                    onComplete(true, BLANK_STRING)
                }
            }

        } catch (ex: Exception) {
            CoreLogger.e(
                tag = "DadaTabUseCase",
                msg = "invoke: exception -> ${ex.message}",
                ex = ex,
                stackTrace = true
            )
            withContext(CoreDispatchers.mainDispatcher) {
                onComplete(true, BLANK_STRING)
            }
        }

    }

}