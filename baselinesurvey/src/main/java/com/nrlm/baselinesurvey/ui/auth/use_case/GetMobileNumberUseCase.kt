package com.nrlm.baselinesurvey.ui.auth.use_case

import com.nrlm.baselinesurvey.ui.auth.repository.OtpVerificationRepository

class GetMobileNumberUseCase(private val repository: OtpVerificationRepository) {

    operator fun invoke(): String {
        return repository.getMobileNumber()
    }

}
