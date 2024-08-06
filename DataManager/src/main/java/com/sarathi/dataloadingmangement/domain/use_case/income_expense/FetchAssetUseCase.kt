package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.repository.liveihood.AssetRepositoryImpl

class FetchAssetUseCase(
    private val assetRepositoryImpl: AssetRepositoryImpl,
) {

    suspend operator fun invoke(livelihoodId: Int): List<ValuesDto> {
        return assetRepositoryImpl.getAssetsForLivelihood(livelihoodId).map {
            ValuesDto(it.id, it.name, false)
        }
    }

}