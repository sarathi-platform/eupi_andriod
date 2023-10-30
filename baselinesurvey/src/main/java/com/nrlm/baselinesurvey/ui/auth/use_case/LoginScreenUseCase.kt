package com.nrlm.baselinesurvey.ui.auth.use_case

data class LoginScreenUseCase(
    val generateOtpUseCase: GenerateOtpUseCase,
    val saveMobileNumberUseCase: SaveMobileNumberUseCase
)
