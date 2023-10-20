package com.nrlm.baselinesurvey.ui.auth.use_case

import com.nrlm.baselinesurvey.model.request.LoginRequest
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.ui.auth.repository.OtpVerificationRepository

class ResendOtpUseCase (private val repository: OtpVerificationRepository) {

    suspend operator fun invoke(mobileNumber: String): ApiResponseModel<String> {
        val loginRequest = LoginRequest(mobileNumber)
        return repository.resendOtp(loginRequest)
    }

}
