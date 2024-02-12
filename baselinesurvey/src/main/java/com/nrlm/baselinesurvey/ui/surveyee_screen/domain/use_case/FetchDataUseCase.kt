package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

data class FetchDataUseCase (
    val fetchUserDetailFromNetworkUseCase: FetchUserDetailFromNetworkUseCase,
    val fetchSurveyeeListFromNetworkUseCase: FetchSurveyeeListFromNetworkUseCase,
    val fetchSurveyFromNetworkUseCase: FetchSurveyFromNetworkUseCase
)
