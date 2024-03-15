package com.nrlm.baselinesurvey.ui.splash.domain.use_case

import com.nrlm.baselinesurvey.ui.splash.domain.repository.SplashScreenRepository

class LoggedInUseCase(private val repository: SplashScreenRepository) {

    operator fun invoke(): Boolean {
        return repository.isLoggedIn()
    }

    fun isDataSynced(): Boolean {
        return repository.isDataSynced()
    }

    fun setDataSynced() {
        return repository.setAllDataSynced()
    }

}