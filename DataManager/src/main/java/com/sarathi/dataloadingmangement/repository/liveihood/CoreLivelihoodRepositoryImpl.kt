package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodEventDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodLanguageDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.ProductDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEventEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodLanguageReferenceEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.ProductEntity
import com.sarathi.dataloadingmangement.model.response.Asset
import com.sarathi.dataloadingmangement.model.response.LanguageReference
import com.sarathi.dataloadingmangement.model.response.Livelihood
import com.sarathi.dataloadingmangement.model.response.LivelihoodEvent
import com.sarathi.dataloadingmangement.model.response.Product
import javax.inject.Inject

class CoreLivelihoodRepositoryImpl @Inject constructor(
    private val assetDao: AssetDao,
    private val productDao: ProductDao,
    private val livelihoodEventDao: LivelihoodEventDao,
    private val livelihoodDao: LivelihoodDao,
    private val livelihoodLanguageDao: LivelihoodLanguageDao,
    private val coreSharedPrefs: CoreSharedPrefs,
) : ICoreLivelihoodRepository {
    override suspend fun <T> saveLivelihoodItemListToDB(items: List<T>) {
        items.forEach { item ->
            when (item) {
                is Asset -> {
                    assetDao.insertAsset(
                        AssetEntity.getAssetEntity(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            asset = item
                        )
                    )
                    saveLivelihoodLanguageToDB(item.languages)
                }

                is Product -> {
                    productDao.insertProduct(
                        ProductEntity.getProductEntity(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            product = item
                        )
                    )
                    saveLivelihoodLanguageToDB(item.languages)
                }

                is LivelihoodEvent -> {
                    livelihoodEventDao.insertLivelihoodEvent(
                        LivelihoodEventEntity.getLivelihoodEventEntity(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            livelihoodEvent = item
                        )
                    )
                    saveLivelihoodLanguageToDB(item.languages)
                }
            }
        }

    }

    override suspend fun <T> saveLivelihoodItemToDB(item: T) {
        when (item) {
            is Livelihood -> {
                livelihoodDao.insertLivelihood(
                    LivelihoodEntity.getLivelihoodEntity(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        livelihood = item
                    )
                )
                saveLivelihoodLanguageToDB(item.languages)
            }
        }
    }

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