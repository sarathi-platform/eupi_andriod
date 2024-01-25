package com.nrlm.baselinesurvey.splash.domain.use_case

import com.nrlm.baselinesurvey.splash.domain.repository.SplashScreenRepository

class LoggedInUseCase(private val repository: SplashScreenRepository) {

    operator fun invoke(): Boolean {
        return repository.isLoggedIn()
    }

}