package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.ApiResponseModel
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
import com.sarathi.dataloadingmangement.enums.LivelihoodLanguageReferenceType
import com.sarathi.dataloadingmangement.model.response.Asset
import com.sarathi.dataloadingmangement.model.response.LanguageReference
import com.sarathi.dataloadingmangement.model.response.Livelihood
import com.sarathi.dataloadingmangement.model.response.LivelihoodEvent
import com.sarathi.dataloadingmangement.model.response.LivelihoodResponse
import com.sarathi.dataloadingmangement.model.response.Product
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class CoreLivelihoodRepositoryImpl @Inject constructor(
    private val assetDao: AssetDao,
    private val productDao: ProductDao,
    private val livelihoodEventDao: LivelihoodEventDao,
    private val livelihoodDao: LivelihoodDao,
    private val livelihoodLanguageDao: LivelihoodLanguageDao,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val dataLoadingApiService: DataLoadingApiService
) : ICoreLivelihoodRepository {
    override suspend fun getLivelihoodConfigFromNetwork(): ApiResponseModel<List<LivelihoodResponse>> {
        val userId = coreSharedPrefs.getUserNameInInt()
        return dataLoadingApiService.fetchLivelihoodConfigData(userId = userId)//coreSharedPrefs.getUserNameInInt())
    }

    override suspend fun <T> saveLivelihoodItemListToDB(items: List<T>, livelihoodId: Int) {
        items.forEach { item ->
            when (item) {
                is Asset -> {
                    assetDao.insertAsset(
                        AssetEntity.getAssetEntity(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            asset = item,
                            livelihoodId = livelihoodId,
                            value = item.value,
                            image = item.image
                        )
                    )
                    saveLivelihoodLanguageToDB(
                        item.languages,
                        LivelihoodLanguageReferenceType.Asset.name
                    )
                }

                is Product -> {
                    productDao.insertProduct(
                        ProductEntity.getProductEntity(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            product = item,
                            livelihoodId = livelihoodId
                        )
                    )
                    saveLivelihoodLanguageToDB(
                        item.languages,
                        LivelihoodLanguageReferenceType.Product.name
                    )
                }

                is LivelihoodEvent -> {
                    livelihoodEventDao.insertLivelihoodEvent(
                        LivelihoodEventEntity.getLivelihoodEventEntity(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            livelihoodEvent = item,
                            livelihoodId = livelihoodId
                        )
                    )
                    saveLivelihoodLanguageToDB(
                        item.languages,
                        LivelihoodLanguageReferenceType.Event.name
                    )
                }
            }
        }

    }

    override suspend fun <T> saveLivelihoodItemToDB(item: T, referenceType: String) {
        when (item) {
            is Livelihood -> {
                livelihoodDao.insertLivelihood(
                    LivelihoodEntity.getLivelihoodEntity(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        livelihood = item
                    )
                )
                saveLivelihoodLanguageToDB(item.languages, referenceType)
            }
        }
    }

    override suspend fun saveLivelihoodLanguageToDB(
        languageReferences: List<LanguageReference>,
        referenceType: String
    ) {
        val languageReferenceEntities = ArrayList<LivelihoodLanguageReferenceEntity>()
        languageReferences.forEach { languageReference ->
            languageReferenceEntities.add(
                LivelihoodLanguageReferenceEntity.getLivelihoodLanguageEntity(
                    uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier(),
                    languageReference = languageReference,
                    referenceType = referenceType
                )
            )
        }
        livelihoodLanguageDao.insertLivelihoodLanguage(languageEntity = languageReferenceEntities)
    }

    override suspend fun deleteLivelihoodCoreDataForUser() {
        assetDao.deleteAssetsForUser(userId = coreSharedPrefs.getUniqueUserIdentifier())
        livelihoodDao.deleteLivelihoodForUser(userId = coreSharedPrefs.getUniqueUserIdentifier())
        productDao.deleteProductForUser(userId = coreSharedPrefs.getUniqueUserIdentifier())
        livelihoodEventDao.deleteLivelihoodEventForUser(userId = coreSharedPrefs.getUniqueUserIdentifier())
    }

}