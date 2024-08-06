package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.events.LivelihoodPlanActivityEventDto

interface ILivelihoodEventRepository {
    fun getSaveLivelihoodEventDto(livelihoodEntity: LivelihoodPlanActivityEventDto): LivelihoodPlanActivityEventDto

}