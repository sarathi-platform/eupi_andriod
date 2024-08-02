package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity
import com.sarathi.dataloadingmangement.model.response.Asset
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val assetDao: AssetDao

) : IAssetRepository {
    override suspend fun saveAssetToDB(asset: Asset) {
        assetDao.insertAsset(AssetEntity.getAssetEntity(userId = "", asset = asset))
    }

}