package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodLanguageDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodLanguageReferenceEntity
import com.sarathi.dataloadingmangement.model.response.LanguageReference
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class LivelihoodLanguageRepositoryImpl @Inject constructor(
    val apiInterface: DataLoadingApiService,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val livelihoodLanguageDao: LivelihoodLanguageDao

) : ILivelihoodLanguageRepository {
    override suspend fun saveLivelihoodLanguageToDB(languageReferences: List<LanguageReference>) {
        val languageReferenceEntities = ArrayList<LivelihoodLanguageReferenceEntity>()
        languageReferences.forEach { languageReference ->
            languageReferenceEntities.add(
                LivelihoodLanguageReferenceEntity.getLivelihoodLanguageEntity(
                    uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier(),
                    languageReference = languageReference
                )
            )
        }
        livelihoodLanguageDao.insertLivelihoodLanguage(languageEntity = languageReferenceEntities)
    }
}