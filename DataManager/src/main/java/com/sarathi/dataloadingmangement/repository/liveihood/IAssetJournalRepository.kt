package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData

interface IAssetJournalRepository {
    suspend fun saveOrEditAssetJournal(particular: String, eventData: LivelihoodEventScreenData)
}