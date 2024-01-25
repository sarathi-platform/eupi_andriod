package com.nrlm.baselinesurvey.splash.domain.use_case

import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.ConfigResponseModel
import com.nrlm.baselinesurvey.splash.domain.repository.SplashScreenRepository
import java.util.concurrent.Flow

class FetchLanguageFromNetworkConfigUseCase(private val splashScreenRepository: SplashScreenRepository) {

    suspend operator fun invoke(): ApiResponseModel<ConfigResponseModel?> {
        return splashScreenRepository.getLanguageConfigFromNetwork()
    }

}