package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import com.sarathi.dataloadingmangement.repository.liveihood.IAssetJournalRepository
import javax.inject.Inject

class SaveLivelihoodEventUseCase @Inject constructor(
    private val assetJournalRepository: IAssetJournalRepository,
) {

    suspend fun addOrEditEvent(eventData: LivelihoodEventScreenData) {
        assetJournalRepository.saveOrEditAssetJournal(particular = "", eventData = eventData)
    }

}