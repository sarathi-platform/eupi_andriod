package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.nudge.core.model.uiModel.LivelihoodModel
import com.sarathi.dataloadingmangement.repository.liveihood.GetLivelihoodListFromDbRepository
import javax.inject.Inject

class GetLivelihoodListFromDbUseCase @Inject constructor(
    val getLivelihoodListFromDbRepository: GetLivelihoodListFromDbRepository
) {

    suspend operator fun invoke(): List<LivelihoodModel> {
        return getLivelihoodListFromDbRepository.getLivelihoodListFromDb()
    }
    suspend operator fun invoke(livelihoodIds: List<Int>): List<LivelihoodModel> {
        return getLivelihoodListFromDbRepository.getLivelihoodListFromDb(livelihoodIds)
    }

}