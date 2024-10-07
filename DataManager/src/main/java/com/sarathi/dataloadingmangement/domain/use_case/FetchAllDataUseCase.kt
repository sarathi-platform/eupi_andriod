package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.usecase.FetchAppConfigFromNetworkUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.domain.use_case.income_expense.FetchLivelihoodSaveEventUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchAssetJournalUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.FetchLivelihoodOptionNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchDidiDetailsFromNetworkUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FetchAllDataUseCase @Inject constructor(
    val fetchMissionDataUseCase: FetchMissionDataUseCase,
    val fetchContentDataFromNetworkUseCase: FetchContentDataFromNetworkUseCase,
    val fetchSurveyDataFromNetworkUseCase: FetchSurveyDataFromNetworkUseCase,
    val fetchDidiDetailsFromNetworkUseCase: FetchDidiDetailsFromNetworkUseCase,
    val contentDownloaderUseCase: ContentDownloaderUseCase,
    val fetchLanguageUseCase: FetchLanguageUseCase,
    val fetchUserDetailUseCase: FetchUserDetailUseCase,
    val fetchSurveyAnswerFromNetworkUseCase: FetchSurveyAnswerFromNetworkUseCase,
    val formUseCase: FormUseCase,
    val moneyJournalUseCase: FetchMoneyJournalUseCase,
    val assetJournalUseCase: FetchAssetJournalUseCase,
    val livelihoodUseCase: LivelihoodUseCase,
    val fetchLivelihoodOptionNetworkUseCase: FetchLivelihoodOptionNetworkUseCase,
    val fetchLivelihoodSaveEventUseCase: FetchLivelihoodSaveEventUseCase,
    val fetchAppConfigFromNetworkUseCase: FetchAppConfigFromNetworkUseCase,
    private val coreSharedPrefs: CoreSharedPrefs
) {


    suspend fun invoke(
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
        isRefresh: Boolean = true
    ) {
        if (isRefresh || !coreSharedPrefs.isDataLoaded()) {
            fetchLanguageUseCase.invoke()
            fetchAppConfigFromNetworkUseCase.invoke()
            fetchUserDetailUseCase.invoke()
            fetchDidiDetailsFromNetworkUseCase.invoke()
            val isMissionDataFetched = fetchMissionDataUseCase.invoke()
            fetchSurveyDataFromNetworkUseCase.invoke()
            if (!isRefresh) {
                fetchSurveyAnswerFromNetworkUseCase.invoke()
                formUseCase.invoke()
                moneyJournalUseCase.invoke()
                if (isMissionDataFetched && fetchMissionDataUseCase.isActivityTypeAvailable(
                        LivelihoodActivityType
                    ) != 0
                ) {
                    fetchLivelihoodSaveEventUseCase.invoke()
                    fetchLivelihoodOptionNetworkUseCase.invoke()
                    assetJournalUseCase.invoke()
                }
            }
            if (isMissionDataFetched && fetchMissionDataUseCase.isActivityTypeAvailable(
                    LivelihoodActivityType
                ) != 0
            ) {
                livelihoodUseCase.invoke()
            }
            fetchContentDataFromNetworkUseCase.invoke()
            coreSharedPrefs.setDataLoaded(true)
            onComplete(true, BLANK_STRING)
            CoroutineScope(Dispatchers.IO).launch {
                contentDownloaderUseCase.contentDownloader()
            }

            CoroutineScope(Dispatchers.IO).launch {
                contentDownloaderUseCase.surveyRelateContentDownlaod()
            }
        } else {
            onComplete(true, BLANK_STRING)
        }
    }

    //TODO Temp code remove after data is fetched from API
    fun getStateId() = coreSharedPrefs.getStateId()
}

const val LivelihoodActivityType = "Livelihood"