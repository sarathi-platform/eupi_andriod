package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.response.LanguageReference

interface ICoreLivelihoodRepository {
    suspend fun <T> saveLivelihoodItemListToDB(items: List<T>)
    suspend fun <T> saveLivelihoodItemToDB(item: T)
    suspend fun saveLivelihoodLanguageToDB(
        languageReferences: List<LanguageReference>
    )

}