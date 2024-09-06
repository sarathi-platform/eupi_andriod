package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.ProductAssetUiModel

interface IAssetRepository {

    suspend fun getAssetsForLivelihood(livelihoodId: Int): List<ProductAssetUiModel>

    suspend fun getAssetsEntityForLivelihood(livelihoodIds: List<Int>): List<AssetEntity>

}