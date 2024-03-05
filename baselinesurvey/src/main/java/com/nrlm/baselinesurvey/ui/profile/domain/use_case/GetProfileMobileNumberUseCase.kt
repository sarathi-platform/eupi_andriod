package com.nrlm.baselinesurvey.ui.profile.domain.use_case

import com.nrlm.baselinesurvey.ui.profile.domain.repository.ProfileBSRepository

class GetProfileMobileNumberUseCase(private val repository:ProfileBSRepository) {
    operator fun invoke():String{
        return repository.getUserMobileNumber()
    }
}