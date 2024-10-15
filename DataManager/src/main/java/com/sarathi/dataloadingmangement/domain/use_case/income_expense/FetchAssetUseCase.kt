package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.ProductAssetUiModel
import com.sarathi.dataloadingmangement.repository.liveihood.AssetRepositoryImpl

class FetchAssetUseCase(
    private val assetRepositoryImpl: AssetRepositoryImpl,
) {

    suspend operator fun invoke(livelihoodId: Int, selectedId: Int): List<ProductAssetUiModel> {
        return assetRepositoryImpl.getAssetsForLivelihood(livelihoodId)
    }

}