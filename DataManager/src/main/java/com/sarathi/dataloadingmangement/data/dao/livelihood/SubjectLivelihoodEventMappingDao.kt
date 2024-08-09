package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.SubjectLivelihoodEventSummaryUiModel

@Dao
interface SubjectLivelihoodEventMappingDao {
    @Insert
    suspend fun insertSubjectLivelihoodEventMapping(subjectLivelihoodEventMappingEntity: SubjectLivelihoodEventMappingEntity)

    @Query("SELECT * from $SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME where subjectId = :subjectId and userId = :userId")
    suspend fun getSubjectLivelihoodEventMappingAvailable(
        subjectId: Int,
        userId: String
    ): List<SubjectLivelihoodEventMappingEntity>?

    @Query(
        "select subject_livelihood_event_mapping_table.transactionId, subject_livelihood_event_mapping_table.subjectId, subject_livelihood_event_mapping_table.date, subject_livelihood_event_mapping_table.livelihoodId, subject_livelihood_event_mapping_table.livelihoodEventId, subject_livelihood_event_mapping_table.livelihoodEventType, \n" +
                "money_journal_table.transactionAmount, money_journal_table.transactionFlow as moneyJournalFlow, \n" +
                "asset_journal_table.assetId, asset_journal_table.assetCount, asset_journal_table.transactionFlow as assetJournalFlow \n" +
                "from subject_livelihood_event_mapping_table\n" +
                "left join money_journal_table on money_journal_table.transactionId = subject_livelihood_event_mapping_table.transactionId\n" +
                "left join asset_journal_table on asset_journal_table.transactionId = subject_livelihood_event_mapping_table.transactionId\n" +
                "where subject_livelihood_event_mapping_table.userId = :userId and subject_livelihood_event_mapping_table.subjectId = :subjectId"
    )
    suspend fun getLivelihoodEventsWithAssetAndMoneyEntryForSubject(
        userId: String,
        subjectId: Int
    ): List<SubjectLivelihoodEventSummaryUiModel>


}