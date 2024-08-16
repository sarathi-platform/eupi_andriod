package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.events.LivelihoodPlanActivityEventDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventUiModel

interface ILivelihoodEventRepository {
    fun getSaveLivelihoodEventDto(livelihoodEntity: LivelihoodPlanActivityEventDto): LivelihoodPlanActivityEventDto

    suspend fun getEventsForLivelihood(livelihoodId: Int): List<LivelihoodEventUiModel>

    suspend fun getEventsForLivelihood(livelihoodIds: List<Int>): List<LivelihoodEventUiModel>
}