package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity

@Dao
interface SubjectLivelihoodEventMappingDao {
    @Insert
    suspend fun insertSubjectLivelihoodEventMapping(subjectLivelihoodEventMappingEntity: SubjectLivelihoodEventMappingEntity)

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

    @Query("Update subject_livelihood_event_mapping_table set status=0 where transactionId=:transactionId and subjectId=:subjectId and userId=:userId")
    suspend fun softDeleteLivelihoodEventMapping(
        userId: String,
        transactionId: String,
        subjectId: Int
    )
}