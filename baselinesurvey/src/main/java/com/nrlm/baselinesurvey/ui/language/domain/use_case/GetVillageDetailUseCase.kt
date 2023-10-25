package com.nrlm.baselinesurvey.ui.language.domain.use_case

import com.nrlm.baselinesurvey.database.entity.VillageEntity
import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepository

class GetVillageDetailUseCase(
    private val repository: LanguageScreenRepository
)  {

    suspend operator fun invoke(villageId: Int, languageId: Int): VillageEntity {
        return repository.fetchVillageDetailsForLanguage(villageId, languageId)
    }

}
