package com.patsurvey.nudge.domain.usecases.steps

import com.patsurvey.nudge.data.prefs.repository.interfaces.StepListRepository
import com.patsurvey.nudge.database.StepListEntity
import javax.inject.Inject

class GetStepListUseCase @Inject constructor(
    private val stepListRepository: StepListRepository
) {

    suspend fun invoke(villageId: Int, stepId: Int): StepListEntity {
        return stepListRepository.getStepList(villageId = villageId, stepId = stepId)
    }

    suspend fun invoke(villageId: Int): List<StepListEntity> {
        return stepListRepository.getStepListForVillage(villageId = villageId)
    }
}