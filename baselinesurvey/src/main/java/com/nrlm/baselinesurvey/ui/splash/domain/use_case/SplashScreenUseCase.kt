package com.nrlm.baselinesurvey.ui.splash.domain.use_case

data class SplashScreenUseCase(
    val fetchLanguageConfigFromNetworkUseCase: FetchLanguageFromNetworkConfigUseCase,
    val saveLanguageConfigUseCase: SaveLanguageConfigUseCase,
    val saveQuestionImageUseCase: SaveQuestionImageUseCase,
    val loggedInUseCase: LoggedInUseCase,
    val saveLanguageOpenFromUseCase: SaveLanguageOpenFromUseCase
)
