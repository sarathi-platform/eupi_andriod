package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case.FetchMissionDataFromNetworkUseCase

data class FetchDataUseCase(
    val fetchUserDetailFromNetworkUseCase: FetchUserDetailFromNetworkUseCase,
    val fetchCastesFromNetworkUseCase: FetchCastesFromNetworkUseCase,
    val fetchSurveyeeListFromNetworkUseCase: FetchSurveyeeListFromNetworkUseCase,
    val fetchSurveyFromNetworkUseCase: FetchSurveyFromNetworkUseCase,
    val fetchMissionDataFromNetworkUseCase: FetchMissionDataFromNetworkUseCase,
    val fetchContentnDataFromNetworkUseCase: FetchContentnDataFromNetworkUseCase
)
