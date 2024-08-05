package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.response.LanguageReference

interface ILivelihoodLanguageRepository {
    suspend fun saveLivelihoodLanguageToDB(
        languageReferences: List<LanguageReference>
    )
}