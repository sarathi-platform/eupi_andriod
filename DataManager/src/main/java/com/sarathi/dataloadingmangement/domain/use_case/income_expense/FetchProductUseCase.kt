package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.repository.liveihood.ProductRepositoryImpl

class FetchProductUseCase(
    private val productRepositoryImpl: ProductRepositoryImpl,
) {

    suspend operator fun invoke(livelihoodId: Int): List<ValuesDto> {
        return productRepositoryImpl.getProductsForLivelihood(livelihoodId).map {
            ValuesDto(it.id, it.name, false)
        }
    }

}