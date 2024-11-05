package com.nrlm.baselinesurvey.ui.language.domain.use_case

import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepository
import com.nudge.core.database.entities.language.LanguageEntity

class GetLanguageListFromDbUseCase(
    private val repository: LanguageScreenRepository
) {

    suspend operator fun invoke(): List<LanguageEntity> {
        return repository.getAllLanguages()
    }

}
