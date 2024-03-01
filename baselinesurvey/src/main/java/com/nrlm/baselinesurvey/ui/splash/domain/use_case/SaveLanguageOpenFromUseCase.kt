package com.nrlm.baselinesurvey.ui.splash.domain.use_case

import com.nrlm.baselinesurvey.ui.splash.domain.repository.SplashScreenRepository

class SaveLanguageOpenFromUseCase(private val splashScreenRepository: SplashScreenRepository) {
     operator fun invoke(){
        splashScreenRepository.saveLanguageOpenFrom()
    }
}