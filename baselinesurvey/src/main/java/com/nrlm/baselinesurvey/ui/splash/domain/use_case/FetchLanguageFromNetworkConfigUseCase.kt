package com.nrlm.baselinesurvey.ui.splash.domain.use_case

import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.ConfigResponseModel
import com.nrlm.baselinesurvey.ui.splash.domain.repository.SplashScreenRepository

class FetchLanguageFromNetworkConfigUseCase(private val splashScreenRepository: SplashScreenRepository) {

    suspend operator fun invoke(): ApiResponseModel<ConfigResponseModel?> {
        return splashScreenRepository.getLanguageConfigFromNetwork()
    }

}