package com.sarathi.dataloadingmangement.domain

import com.sarathi.dataloadingmangement.domain.use_case.FetchContentDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromNetworkUseCase


data class DataLoadingUseCase(
    val fetchMissionDataFromNetworkUseCase: FetchMissionDataUseCase,
    val fetchContentDataFromNetworkUseCase: FetchContentDataFromNetworkUseCase,
    val fetchSurveyDataFromNetworkUseCase: FetchSurveyDataFromNetworkUseCase
)