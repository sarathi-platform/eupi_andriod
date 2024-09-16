package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.BLANK_STRING
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetJournalDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetJournalEntity
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveAssetJournalEventDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import javax.inject.Inject

class AssetJournalRepositoryImpl @Inject constructor(
    private val assetJournalDao: AssetJournalDao,
    private val coreSharedPrefs: CoreSharedPrefs
) :
    IAssetJournalRepository {
    override suspend fun saveOrEditAssetJournal(
        particular: String,
        eventData: LivelihoodEventScreenData,
        createdDate: Long
    ) {
        val assetJournal = AssetJournalEntity.getAssetJournalEntity(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            count = eventData.assetCount,
            date = eventData.date,
            particulars = particular,
            transactionId = eventData.transactionId,
            subjectType = "Didi",
            subjectId = eventData.subjectId,
            transactionFlow = eventData.selectedEvent.assetJournalEntryFlowType?.name
                ?: BLANK_STRING,
            referenceType = "LivelihoodEvent",
            referenceId = eventData.livelihoodId,
            assetId = eventData.assetType,
            createdDate = createdDate
        )

        assetJournalDao.insetAssetJournalEntry(assetJournal)


    }

    override suspend fun softDeleteAssetJournalEvent(transactionId: String, subjectId: Int) {
        if (assetJournalDao.isTransactionAlreadyExist(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionId = transactionId,
                subjectId = subjectId
            ) > 0
        ) {
            assetJournalDao.softDeleteTransaction(
                transactionId = transactionId,
                subjectId = subjectId,
                userId = coreSharedPrefs.getUniqueUserIdentifier()
            )
        }

    }

    override suspend fun getAssetForTransaction(
        transactionId: String,
        subjectId: Int
    ): AssetJournalEntity? {
        return assetJournalDao.getAssetJournalForTransaction(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            transactionId = transactionId,
            subjectId = subjectId
        )
    }

    override suspend fun getSaveAssetJournalEventDto(
        particular: String,
        eventData: LivelihoodEventScreenData,
        currentDateTime: Long,
        modifiedDateTIme: Long
    ): SaveAssetJournalEventDto {
        return SaveAssetJournalEventDto(
            assetCount = eventData.assetCount,
            createdDate = currentDateTime,
            particulars = particular,
            referenceId = eventData.livelihoodId,
            subjectId = eventData.subjectId,
            subjectType = "Didi",
            status = 1,
            transactionId = eventData.transactionId,
            referenceType = "LivelihoodEvent",
            transactionType = "LivelihoodEvent",
            transactionDate = eventData.date,
            transactionFlow = eventData.selectedEvent.assetJournalEntryFlowType?.name
                ?: BLANK_STRING,
            assetId = eventData.assetType,
            modifiedDate = modifiedDateTIme
        )
    }

    override suspend fun getAllAssetJournalForUser(): List<AssetJournalEntity> {
        return assetJournalDao.getAssetJournalForUser(coreSharedPrefs.getUniqueUserIdentifier())
    }
}