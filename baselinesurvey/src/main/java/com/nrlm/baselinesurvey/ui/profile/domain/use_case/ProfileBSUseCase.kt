package com.nrlm.baselinesurvey.ui.profile.domain.use_case


data class ProfileBSUseCase(
    val getUserNameUseCase:GetUserNameUseCase,
    val getUserEmailUseCase: GetUserEmailUseCase,
    val getUserMobileNumberUseCase: GetProfileMobileNumberUseCase,
    val getIdentityNumberUseCase: GetIdentityNumberUseCase
)