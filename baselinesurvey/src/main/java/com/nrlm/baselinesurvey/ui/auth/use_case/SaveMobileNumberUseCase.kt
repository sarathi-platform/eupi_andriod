package com.nrlm.baselinesurvey.ui.auth.use_case

import com.nrlm.baselinesurvey.ui.auth.repository.LoginScreenRepository

class SaveMobileNumberUseCase(private val repository: LoginScreenRepository) {

    operator fun invoke(mobileNumber: String) {
        repository.saveMobileNumber(mobileNumber)
    }
}
