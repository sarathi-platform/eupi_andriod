package com.nrlm.baselinesurvey.ui.language.use_case

import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.ui.language.repository.LanguageScreenRepository

class GetLanguageListFromDbUseCase(
    private val repository: LanguageScreenRepository
) {

    suspend operator fun invoke(): List<LanguageEntity> {
        return repository.getAllLanguages()
    }

}
