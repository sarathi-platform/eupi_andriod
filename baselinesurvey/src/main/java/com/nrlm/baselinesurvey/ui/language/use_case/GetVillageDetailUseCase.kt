package com.nrlm.baselinesurvey.ui.language.use_case

import com.nrlm.baselinesurvey.database.entity.VillageEntity
import com.nrlm.baselinesurvey.ui.language.repository.LanguageScreenRepository

class GetVillageDetailUseCase(
    private val repository: LanguageScreenRepository
)  {

    suspend operator fun invoke(villageId: Int, languageId: Int): VillageEntity {
        return repository.fetchVillageDetailsForLanguage(villageId, languageId)
    }

}
