package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.BLANK_STRING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FetchAllDataUseCase @Inject constructor(
    val fetchMissionDataUseCase: FetchMissionDataUseCase,
    val fetchContentDataFromNetworkUseCase: FetchContentDataFromNetworkUseCase,
    val fetchSurveyDataFromNetworkUseCase: FetchSurveyDataFromNetworkUseCase,
    val contentDownloaderUseCase: ContentDownloaderUseCase,
    val fetchLanguageUseCase: FetchLanguageUseCase,
    val fetchUserDetailUseCase: FetchUserDetailUseCase,
    val fetchSurveyAnswerFromNetworkUseCase: FetchSurveyAnswerFromNetworkUseCase
) {
    suspend fun invoke(onComplete: (isSuccess: Boolean, successMsg: String) -> Unit) {
        fetchLanguageUseCase.invoke()
        fetchUserDetailUseCase.invoke()
        fetchMissionDataUseCase.invoke()
        fetchSurveyDataFromNetworkUseCase.invoke()
        fetchContentDataFromNetworkUseCase.invoke()
        //   fetchSurveyAnswerFromNetworkUseCase.invoke()
        onComplete(true, BLANK_STRING)
        CoroutineScope(Dispatchers.IO).launch {
            contentDownloaderUseCase.contentDownloader()
        }
        CoroutineScope(Dispatchers.IO).launch {
            contentDownloaderUseCase.surveyRelateContentDownlaod()
        }
    }
}