package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.events.LivelihoodPlanActivityEventDto
import javax.inject.Inject

class LivelihoodEventRepositoryImpl @Inject constructor() : ILivelihoodEventRepository {
    override fun getSaveLivelihoodEventDto(
        livelihoodPlanActivityEventDto: LivelihoodPlanActivityEventDto
    ): LivelihoodPlanActivityEventDto {
        return  LivelihoodPlanActivityEventDto(
            userId = livelihoodPlanActivityEventDto.userId,
            primaryLivelihoodId = livelihoodPlanActivityEventDto.primaryLivelihoodId,
            secondaryLivelihoodId = livelihoodPlanActivityEventDto.secondaryLivelihoodId,
            activityId = livelihoodPlanActivityEventDto.activityId,
            missionId = livelihoodPlanActivityEventDto.missionId,
            subjectId = livelihoodPlanActivityEventDto.subjectId,
            subjectType = livelihoodPlanActivityEventDto.subjectType
        )
    }
}