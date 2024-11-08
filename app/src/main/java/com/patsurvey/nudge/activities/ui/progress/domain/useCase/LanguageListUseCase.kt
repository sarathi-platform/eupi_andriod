package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.LanguageListRepository
import com.patsurvey.nudge.database.LanguageEntity
import javax.inject.Inject

class LanguageListUseCase @Inject constructor(
    private val languageListRepository: LanguageListRepository
) {

    suspend operator fun invoke(): List<LanguageEntity> {
        return languageListRepository.getAllLanguage()
    }

}