package com.nrlm.baselinesurvey.ui.auth.use_case

import com.nrlm.baselinesurvey.ui.auth.repository.OtpVerificationRepository

class SaveAccessTokenUseCase(private val repository: OtpVerificationRepository) {

    operator fun invoke(token: String) {
        repository.saveAccessToken(token)
    }

    fun saveUserType(userType: String) {
        repository.saveUserType(userType)
    }

}
