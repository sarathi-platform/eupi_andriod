package com.sarathi.dataloadingmangement.domain

import com.sarathi.dataloadingmangement.domain.use_case.FetchContentDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionActivityDetailDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromNetworkUseCase


data class DataLoadingUseCase(
    val fetchMissionDataFromNetworkUseCase: FetchMissionActivityDetailDataUseCase,
    val fetchMissionDataUseCase: FetchMissionDataUseCase,
    val fetchContentDataFromNetworkUseCase: FetchContentDataFromNetworkUseCase,
    val fetchSurveyDataFromNetworkUseCase: FetchSurveyDataFromNetworkUseCase,
    val fetchSurveyDataFromDB: FetchSurveyDataFromDB
)