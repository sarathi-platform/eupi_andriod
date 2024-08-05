package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodModel
import com.sarathi.dataloadingmangement.repository.liveihood.GetLivelihoodListFromDbRepository
import javax.inject.Inject

class GetLivelihoodListFromDbUseCase @Inject constructor(
    val getLivelihoodListFromDbRepository: GetLivelihoodListFromDbRepository
) {

    suspend operator fun invoke(): List<LivelihoodModel> {
        return getLivelihoodListFromDbRepository.getLivelihoodListFromDb()
    }

}