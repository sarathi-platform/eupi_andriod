package com.nrlm.baselinesurvey.splash.domain.use_case

import com.nrlm.baselinesurvey.splash.domain.repository.SplashScreenRepository

data class SplashScreenUseCase(
    val fetchLanguageConfigFromNetworkUseCase: FetchLanguageFromNetworkConfigUseCase,
    val saveLanguageConfigUseCase: SaveLanguageConfigUseCase,
    val saveQuestionImageUseCase: SaveQuestionImageUseCase,
    val loggedInUseCase: LoggedInUseCase
)
