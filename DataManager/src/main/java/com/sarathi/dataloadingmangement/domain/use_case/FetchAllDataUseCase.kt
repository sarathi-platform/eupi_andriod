package com.sarathi.dataloadingmangement.domain.use_case

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FetchAllDataUseCase @Inject constructor(
    val fetchMissionDataFromNetworkUseCase: FetchMissionDataFromNetworkUseCase,
    val fetchContentDataFromNetworkUseCase: FetchContentDataFromNetworkUseCase,
    val fetchSurveyDataFromNetworkUseCase: FetchSurveyDataFromNetworkUseCase,
    val contentDownloaderUseCase: ContentDownloaderUseCase
) {
    suspend fun invoke(onComplete: (isSuccess: Boolean, successMsg: String) -> Unit) {
        fetchMissionDataFromNetworkUseCase.invoke()
        fetchSurveyDataFromNetworkUseCase.invoke()
        CoroutineScope(Dispatchers.IO).launch {
            fetchContentDataFromNetworkUseCase.invoke()
        }
        CoroutineScope(Dispatchers.IO).launch {
            contentDownloaderUseCase.contentDownloader()
        }
        onComplete(true, "")
    }
}