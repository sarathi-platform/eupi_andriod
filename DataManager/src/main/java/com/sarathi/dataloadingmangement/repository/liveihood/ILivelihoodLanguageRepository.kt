package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodLanguageReferenceEntity

interface ILivelihoodLanguageRepository {
    suspend fun saveLivelihoodLanguageToDB(
        livelihoodLanguages: LivelihoodLanguageReferenceEntity
    )
}