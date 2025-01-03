package com.nrlm.baselinesurvey.ui.splash.domain.use_case

import com.nrlm.baselinesurvey.ui.splash.domain.repository.SplashScreenRepository
import com.nudge.core.database.entities.language.LanguageEntity

class SaveLanguageConfigUseCase(private val splashScreenRepository: SplashScreenRepository) {

    suspend operator fun invoke(languageEntityList: List<LanguageEntity>) {
        splashScreenRepository.saveLanguageIntoDatabase(languageEntityList)
    }

    suspend fun addDefaultLanguage() {
        splashScreenRepository.checkAndAddDefaultLanguage()
    }
}