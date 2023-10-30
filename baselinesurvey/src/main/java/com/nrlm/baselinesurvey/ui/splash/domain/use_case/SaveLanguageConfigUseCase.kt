package com.nrlm.baselinesurvey.splash.domain.use_case

import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.model.response.ConfigResponseModel
import com.nrlm.baselinesurvey.splash.domain.repository.SplashScreenRepository
import com.nrlm.baselinesurvey.utils.getDefaultLanguage

class SaveLanguageConfigUseCase(private val splashScreenRepository: SplashScreenRepository) {

    suspend operator fun invoke(languageEntityList: List<LanguageEntity>) {
        splashScreenRepository.saveLanguageIntoDatabase(languageEntityList)
    }

    suspend fun addDefaultLanguage() {
        splashScreenRepository.checkAndAddDefaultLanguage()
    }
}