package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nudge.core.getCurrentTimeInMillis
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.SubjectLivelihoodEventSummaryUiModel

@Dao
interface SubjectLivelihoodEventMappingDao {
    @Insert
    suspend fun insertSubjectLivelihoodEventMapping(subjectLivelihoodEventMappingEntity: SubjectLivelihoodEventMappingEntity)

    @Insert
    suspend fun insertSubjectLivelihoodEventMapping(subjectLivelihoodEventMappingEntity: List<SubjectLivelihoodEventMappingEntity>)

    @Query("SELECT * from $SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME where subjectId = :subjectId and userId = :userId and status=1")
    suspend fun getSubjectLivelihoodEventMappingAvailable(
        subjectId: Int,
        userId: String
    ): List<SubjectLivelihoodEventMappingEntity>?

    @Query("SELECT * from $SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME where subjectId = :subjectId and userId = :userId and status=1 and transactionId=:transactionId")
    suspend fun getSubjectLivelihoodEventMappingAvailable(
        subjectId: Int,
        transactionId: String,
        userId: String
    ): SubjectLivelihoodEventMappingEntity?

    @Query("Select count(*) from subject_livelihood_event_mapping_table where userId=:userId and transactionId=:transactionId and status=1 ")
    suspend fun isLivelihoodEventMappingExist(userId: String, transactionId: String): Int

    @Query("Update subject_livelihood_event_mapping_table set livelihoodId=:livelihoodId, livelihoodEventId=:livelihoodEventId,livelihoodEventType=:livelihoodEventType where transactionId=:transactionId and subjectId=:subjectId and userId=:userId")
    suspend fun updateLivelihoodEventMapping(
        userId: String, transactionId: String, livelihoodId: Int, livelihoodEventId: Int,
        livelihoodEventType: String, subjectId: Int
    )

    @Query("Update subject_livelihood_event_mapping_table set status = 2, modifiedDate=:modifiedDate where transactionId=:transactionId and subjectId=:subjectId and userId=:userId and status=1")
    suspend fun softDeleteLivelihoodEventMapping(
        userId: String,
        transactionId: String,
        subjectId: Int,
        modifiedDate: Long = getCurrentTimeInMillis()
    )

    @Query(
        "select subject_livelihood_event_mapping_table.transactionId, subject_livelihood_event_mapping_table.subjectId, subject_livelihood_event_mapping_table.date, subject_livelihood_event_mapping_table.livelihoodId, subject_livelihood_event_mapping_table.livelihoodEventId, subject_livelihood_event_mapping_table.livelihoodEventType, \n" +
                "money_journal_table.transactionAmount, money_journal_table.transactionFlow as moneyJournalFlow, \n" +
                "asset_journal_table.assetId, asset_journal_table.assetCount, asset_journal_table.transactionFlow as assetJournalFlow ,\n" +
                "subject_livelihood_event_mapping_table.status " +
                "from subject_livelihood_event_mapping_table\n" +
                "left join money_journal_table on money_journal_table.transactionId = subject_livelihood_event_mapping_table.transactionId and money_journal_table.status=1 \n" +
                "left join asset_journal_table on asset_journal_table.transactionId = subject_livelihood_event_mapping_table.transactionId and asset_journal_table.status=1 \n" +
                "where subject_livelihood_event_mapping_table.userId = :userId and subject_livelihood_event_mapping_table.subjectId = :subjectId and subject_livelihood_event_mapping_table.status=1   group by subject_livelihood_event_mapping_table.id "
    )
    suspend fun getLivelihoodEventsWithAssetAndMoneyEntryForSubject(
        userId: String,
        subjectId: Int
    ): List<SubjectLivelihoodEventSummaryUiModel>

    @Query(
        "select subject_livelihood_event_mapping_table.transactionId, subject_livelihood_event_mapping_table.subjectId, subject_livelihood_event_mapping_table.date, subject_livelihood_event_mapping_table.livelihoodId, subject_livelihood_event_mapping_table.livelihoodEventId, subject_livelihood_event_mapping_table.livelihoodEventType, \n" +
                "money_journal_table.transactionAmount, money_journal_table.transactionFlow as moneyJournalFlow, \n" +
                "asset_journal_table.assetId, asset_journal_table.assetCount, asset_journal_table.transactionFlow as assetJournalFlow ,\n" +
                "subject_livelihood_event_mapping_table.status ,\n" + "Max(subject_livelihood_event_mapping_table.modifiedDate) ,\n" + "Max(asset_journal_table.modifiedDate) ,\n" + "Max(money_journal_table.modifiedDate)" +
                "from subject_livelihood_event_mapping_table\n" +
                "left join money_journal_table on money_journal_table.transactionId = subject_livelihood_event_mapping_table.transactionId and money_journal_table.status=2 \n" +
                "left join asset_journal_table on asset_journal_table.transactionId = subject_livelihood_event_mapping_table.transactionId and asset_journal_table.status=2 \n" +
                "where subject_livelihood_event_mapping_table.userId = :userId and subject_livelihood_event_mapping_table.subjectId = :subjectId  GROUP BY subject_livelihood_event_mapping_table.transactionId\n" +
                "HAVING COUNT(*) = SUM(CASE WHEN subject_livelihood_event_mapping_table.status = 2 THEN 1 ELSE 0 END) order by subject_livelihood_event_mapping_table.modifiedDate desc\n"
    )
    suspend fun getLivelihoodEventsWithAssetAndMoneyEntryForDeletedSubject(
        userId: String,
        subjectId: Int
    ): List<SubjectLivelihoodEventSummaryUiModel>

    @Query("SELECT date from subject_livelihood_event_mapping_table where subjectId = :subjectId and userId = :userId and status = 1 order by date DESC limit 1")
    suspend fun getLastEventDateForSubjectLivelihoodEventMapping(
        userId: String,
        subjectId: Int
    ): Long?

    @Query("DELETE from subject_livelihood_event_mapping_table where userId = :userId")
    fun deleteSubjectLivelihoodEventMappingForUser(userId: String)

    @Query("SELECT * from $SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME where transactionId = :transactionId and userId = :userId order by modifiedDate DESC")
    suspend fun getSubjectLivelihoodEventMappingListForTransactionIdFromDb(
        transactionId: String,
        userId: String
    ): List<SubjectLivelihoodEventMappingEntity>?
}