package com.nrlm.baselinesurvey.ui.splash.domain.use_case

import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.ui.splash.domain.repository.SplashScreenRepository

class SaveLanguageConfigUseCase(private val splashScreenRepository: SplashScreenRepository) {

    suspend operator fun invoke(languageEntityList: List<LanguageEntity>) {
        splashScreenRepository.saveLanguageIntoDatabase(languageEntityList)
    }

    suspend fun addDefaultLanguage() {
        splashScreenRepository.checkAndAddDefaultLanguage()
    }
}