package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.response.LanguageReference
import com.sarathi.dataloadingmangement.model.response.LivelihoodResponse

interface ICoreLivelihoodRepository {
    suspend fun getLivelihoodConfigFromNetwork(): com.nudge.core.model.ApiResponseModel<List<LivelihoodResponse>>
    suspend fun <T> saveLivelihoodItemListToDB(items: List<T>, livelihoodId: Int)
    suspend fun <T> saveLivelihoodItemToDB(item: T, referenceType: String)
    suspend fun saveLivelihoodLanguageToDB(
        languageReferences: List<LanguageReference>,
        referenceType: String
    )

}