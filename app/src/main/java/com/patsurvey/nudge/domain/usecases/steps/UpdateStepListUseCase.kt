package com.patsurvey.nudge.domain.usecases.steps

import com.patsurvey.nudge.data.prefs.repository.interfaces.StepListRepository
import com.patsurvey.nudge.database.StepListEntity
import javax.inject.Inject

class UpdateStepListUseCase @Inject constructor(
    private val stepListRepository: StepListRepository
) {

    suspend fun invoke(villageId: Int, stepId: Int, stepStatus: Int) {
        stepListRepository.updateStepStatus(
            villageId = villageId,
            stepId = stepId,
            stepStatus = stepStatus
        )
    }

    suspend fun invoke(villageId: Int): List<StepListEntity> {
        return stepListRepository.getStepListForVillage(villageId = villageId)
    }
}