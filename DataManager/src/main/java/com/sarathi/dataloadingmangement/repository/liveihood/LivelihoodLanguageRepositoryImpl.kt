package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodLanguageDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodLanguageReferenceEntity
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class LivelihoodLanguageRepositoryImpl @Inject constructor(
    val apiInterface: DataLoadingApiService,
    private val livelihoodLanguageDao: LivelihoodLanguageDao

) : ILivelihoodLanguageRepository {
    override suspend fun saveLivelihoodLanguageToDB(livelihoodLanguages: LivelihoodLanguageReferenceEntity) {
        livelihoodLanguageDao.insertLivelihoodLanguage(languageEntity = livelihoodLanguages)
    }
}