package com.nrlm.baselinesurvey.ui.language.use_case

import com.nrlm.baselinesurvey.database.entity.VillageEntity
import com.nrlm.baselinesurvey.ui.language.repository.LanguageScreenRepository

class GetSelectedVillageUseCase(
    private val repository: LanguageScreenRepository
)  {

    operator fun invoke(): VillageEntity {
        return repository.getSelectedVillage()
    }

}
