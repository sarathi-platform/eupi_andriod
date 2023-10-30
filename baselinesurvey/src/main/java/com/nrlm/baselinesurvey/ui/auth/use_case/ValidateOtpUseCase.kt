package com.nrlm.baselinesurvey.ui.auth.use_case

import com.nrlm.baselinesurvey.model.request.OtpRequest
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.OtpVerificationModel
import com.nrlm.baselinesurvey.ui.auth.repository.OtpVerificationRepository

class ValidateOtpUseCase(private val repository: OtpVerificationRepository) {

    suspend operator fun invoke(otpRequest: OtpRequest): ApiResponseModel<OtpVerificationModel> {
        return repository.validateOtp(otpRequest)
    }

}
