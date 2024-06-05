package com.sarathi.dataloadingmangement.domain.use_case

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
) {
    suspend fun invoke(onComplete: (isSuccess: Boolean, successMsg: String) -> Unit) {
        fetchLanguageUseCase.invoke()
        fetchUserDetailUseCase.invoke()
        fetchMissionDataUseCase.invoke()
        fetchSurveyDataFromNetworkUseCase.invoke()
        fetchContentDataFromNetworkUseCase.invoke()
        onComplete(true, "")
        CoroutineScope(Dispatchers.IO).launch {
            contentDownloaderUseCase.contentDownloader()
        }
    }
}