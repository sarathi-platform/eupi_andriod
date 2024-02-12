package com.nrlm.baselinesurvey.ui.auth.use_case

import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.ui.auth.repository.LoginScreenRepository

class GenerateOtpUseCase(
    private val repository: LoginScreenRepository
) {

    suspend operator fun invoke(mobileNumber: String): ApiResponseModel<String> {
        return repository.generateOtp(mobileNumber)
    }

}
