package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
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
    val livelihoodUseCase: LivelihoodUseCase,
    val fetchLivelihoodOptionNetworkUseCase: FetchLivelihoodOptionNetworkUseCase,
    private val coreSharedPrefs: CoreSharedPrefs
) {


    suspend fun invoke(
        onComplete: (isSuccess: Boolean, successMsg: String) -> Unit,
        isRefresh: Boolean = true
    ) {
        if (isRefresh || !coreSharedPrefs.isDataLoaded()) {
            fetchLanguageUseCase.invoke()
            fetchUserDetailUseCase.invoke()
            fetchDidiDetailsFromNetworkUseCase.invoke()
            fetchMissionDataUseCase.invoke()
            fetchSurveyDataFromNetworkUseCase.invoke()
            if (!isRefresh) {
                fetchSurveyAnswerFromNetworkUseCase.invoke()
                formUseCase.invoke()
            }
            fetchContentDataFromNetworkUseCase.invoke()
            coreSharedPrefs.setDataLoaded(true)
            onComplete(true, BLANK_STRING)
            CoroutineScope(Dispatchers.IO).launch {
                contentDownloaderUseCase.contentDownloader()
            }
            fetchLivelihoodOptionNetworkUseCase.invoke()
            CoroutineScope(Dispatchers.IO).launch {
                contentDownloaderUseCase.surveyRelateContentDownlaod()
            }
            CoroutineScope(Dispatchers.IO).launch {
                livelihoodUseCase.invoke()
            }
        } else {
            onComplete(true, BLANK_STRING)
        }
    }
}