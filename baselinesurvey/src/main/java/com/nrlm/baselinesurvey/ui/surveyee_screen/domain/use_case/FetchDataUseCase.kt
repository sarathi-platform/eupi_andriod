package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case.FetchMissionDataFromNetworkUseCase
import com.nrlm.baselinesurvey.ui.splash.domain.use_case.FetchLanguageFromNetworkConfigUseCase
import com.nrlm.baselinesurvey.ui.splash.domain.use_case.LoggedInUseCase
import com.nrlm.baselinesurvey.ui.splash.domain.use_case.SaveLanguageConfigUseCase

data class FetchDataUseCase(
    val saveLanguageConfigUseCase: SaveLanguageConfigUseCase,
    val fetchLanguageConfigFromNetworkUseCase: FetchLanguageFromNetworkConfigUseCase,
    val fetchUserDetailFromNetworkUseCase: FetchUserDetailFromNetworkUseCase,
    val fetchCastesFromNetworkUseCase: FetchCastesFromNetworkUseCase,
    val fetchSurveyeeListFromNetworkUseCase: FetchSurveyeeListFromNetworkUseCase,
    val fetchSurveyFromNetworkUseCase: FetchSurveyFromNetworkUseCase,
    val fetchMissionDataFromNetworkUseCase: FetchMissionDataFromNetworkUseCase,
    val fetchContentnDataFromNetworkUseCase: FetchContentDataFromNetworkUseCase,
    val fetchSectionStatusFromNetworkUseCase: FetchSectionStatusFromNetworkUseCase,
    val fetchSurveyAnswerFromNetworkUseCase: FetchSurveyAnswerFromNetworkUseCase,
    val loggedInUseCase: LoggedInUseCase
)
