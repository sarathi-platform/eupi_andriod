package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventUiModel
import com.sarathi.dataloadingmangement.repository.liveihood.LivelihoodEventRepositoryImpl
import javax.inject.Inject

class FetchLivelihoodEventUseCase @Inject constructor(
    private val eventRepositoryImpl: LivelihoodEventRepositoryImpl,
) {

    suspend operator fun invoke(livelihoodId: Int): List<LivelihoodEventUiModel> {
        return eventRepositoryImpl.getEventsForLivelihood(livelihoodId)
    }

    suspend operator fun invoke(livelihoodIds: List<Int>): List<LivelihoodEventUiModel> {
        return eventRepositoryImpl.getEventsForLivelihood(livelihoodIds)
    }

}