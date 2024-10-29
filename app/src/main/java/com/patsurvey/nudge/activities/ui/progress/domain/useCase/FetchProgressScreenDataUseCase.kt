package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchProgressScreenDataRepository
import com.patsurvey.nudge.database.StepListEntity
import javax.inject.Inject

class FetchProgressScreenDataUseCase @Inject constructor(
    private val fetchProgressScreenDataRepository: FetchProgressScreenDataRepository
) {

    suspend operator fun invoke(villageId: Int): List<StepListEntity> {
        return fetchProgressScreenDataRepository.getStepListForVillage(villageId)
    }

    suspend fun getStepSummaryForVillage(villageId: Int): Map<String, Int> {
        return fetchProgressScreenDataRepository.getStepSummaryForVillage(villageId)
    }


}