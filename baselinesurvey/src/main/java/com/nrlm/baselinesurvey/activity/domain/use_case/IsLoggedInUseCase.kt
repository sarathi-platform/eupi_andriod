package com.nrlm.baselinesurvey.activity.domain.use_case

import com.nrlm.baselinesurvey.activity.domain.repository.MainActivityRepository

class IsLoggedInUseCase(
    private val repository: MainActivityRepository
) {

    operator fun invoke(): Boolean{
        return repository.isLoggedIn()
    }

}