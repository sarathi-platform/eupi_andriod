package com.nrlm.baselinesurvey.ui.language.domain.use_case

import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepository

class GetLanguageListFromDbUseCase(
    private val repository: LanguageScreenRepository
) {

    suspend operator fun invoke(): List<LanguageEntity> {
        return repository.getAllLanguages()
    }

}
