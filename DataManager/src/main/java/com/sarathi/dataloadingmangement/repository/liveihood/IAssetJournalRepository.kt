package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetJournalEntity
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveAssetJournalEventDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData

interface IAssetJournalRepository {
    suspend fun saveOrEditAssetJournal(particular: String, eventData: LivelihoodEventScreenData)
    suspend fun softDeleteAssetJournalEvent(transactionId: String, subjectId: Int)
    suspend fun getAssetForTransaction(transactionId: String, subjectId: Int): AssetJournalEntity?
    suspend fun getSaveAssetJournalEventDto(
        particular: String,
        eventData: LivelihoodEventScreenData,
        currentDateTime: Long,
        modifiedDateTIme: Long
    ): SaveAssetJournalEventDto

    suspend fun getAllAssetJournalForUser(): List<AssetJournalEntity>
}