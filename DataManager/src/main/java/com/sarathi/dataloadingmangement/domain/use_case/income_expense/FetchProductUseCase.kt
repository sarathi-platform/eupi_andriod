package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.ProductAssetUiModel
import com.sarathi.dataloadingmangement.repository.liveihood.ProductRepositoryImpl

class FetchProductUseCase(
    private val productRepositoryImpl: ProductRepositoryImpl,
) {

    suspend operator fun invoke(livelihoodId: Int): List<ProductAssetUiModel> {
        return productRepositoryImpl.getProductsForLivelihood(livelihoodId)
    }

}