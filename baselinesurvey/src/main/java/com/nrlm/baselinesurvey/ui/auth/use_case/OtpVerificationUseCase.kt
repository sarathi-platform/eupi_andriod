package com.nrlm.baselinesurvey.ui.auth.use_case

data class OtpVerificationUseCase(
    val validateOtpUseCase: ValidateOtpUseCase,
    val saveAccessTokenUseCase: SaveAccessTokenUseCase,
    val resendOtpUseCase: ResendOtpUseCase,
    val getMobileNumberUseCase: GetMobileNumberUseCase,
)
