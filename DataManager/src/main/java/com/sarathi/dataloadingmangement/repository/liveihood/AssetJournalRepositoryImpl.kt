package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.BLANK_STRING
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetJournalDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetJournalEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import javax.inject.Inject

class AssetJournalRepositoryImpl @Inject constructor(
    private val assetJournalDao: AssetJournalDao,
    private val coreSharedPrefs: CoreSharedPrefs
) :
    IAssetJournalRepository {
    override suspend fun saveOrEditAssetJournal(
        particular: String,
        eventData: LivelihoodEventScreenData
    ) {
        assetJournalDao.insetAssetJournalEntry(
            AssetJournalEntity.getAssetJournalEntity(
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
                referenceId = eventData.livelihoodId
            )
        )
    }
}