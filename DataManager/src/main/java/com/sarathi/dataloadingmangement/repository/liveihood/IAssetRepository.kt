package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.ProductAssetUiModel

interface IAssetRepository {

    suspend fun getAssetsForLivelihood(livelihoodId: Int): List<ProductAssetUiModel>

}