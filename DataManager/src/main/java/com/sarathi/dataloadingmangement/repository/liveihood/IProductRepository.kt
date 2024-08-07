package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.ProductAssetUiModel

interface IProductRepository {

    suspend fun getProductsForLivelihood(livelihoodId: Int): List<ProductAssetUiModel>

}