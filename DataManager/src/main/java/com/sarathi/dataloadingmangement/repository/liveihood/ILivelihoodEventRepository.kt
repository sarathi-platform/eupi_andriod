package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventUiModel

interface ILivelihoodEventRepository {

    suspend fun getEventsForLivelihood(livelihoodId: Int): List<LivelihoodEventUiModel>
}