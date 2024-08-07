package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.EventProductAssetUiModel

interface IProductRepository {

    suspend fun getProductsForLivelihood(livelihoodId: Int): List<EventProductAssetUiModel>

}