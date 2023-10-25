package com.nrlm.baselinesurvey.ui.language.domain.use_case

import com.nrlm.baselinesurvey.database.entity.VillageEntity
import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepository

class GetSelectedVillageUseCase(
    private val repository: LanguageScreenRepository
)  {

    operator fun invoke(): VillageEntity {
        return repository.getSelectedVillage()
    }

}
