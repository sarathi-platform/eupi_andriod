package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.EventProductAssetUiModel

interface IAssetRepository {

    suspend fun getAssetsForLivelihood(livelihoodId: Int): List<EventProductAssetUiModel>

}