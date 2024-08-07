package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetDao
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.EventProductAssetUiModel
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val assetDao: AssetDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : IAssetRepository {
    override suspend fun getAssetsForLivelihood(livelihoodId: Int): List<EventProductAssetUiModel> {
        return assetDao.getAssetForLivelihood(
            livelihoodId = livelihoodId,
            languageCode = coreSharedPrefs.getAppLanguage(),
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

}