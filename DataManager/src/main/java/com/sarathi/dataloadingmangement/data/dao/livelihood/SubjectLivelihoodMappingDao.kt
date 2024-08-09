package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity

@Dao
interface SubjectLivelihoodMappingDao {

    @Insert
    suspend fun insertSubjectLivelihoodMapping(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSubjectLivehoodMapping(subjectLivelihoodMappingEntity: List<SubjectLivelihoodMappingEntity>)

    @Query("UPDATE $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME set primaryLivelihoodId = :primaryLivelihoodId where userId = :userId and subjectId = :subjectId")
    suspend fun updatePrimaryLivelihoodForSubject(
        subjectId: Int,
        userId: String,
        primaryLivelihoodId: Int
    )

    @Query("UPDATE $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME set secondaryLivelihoodId = :secondaryLivelihoodId where userId = :userId and subjectId = :subjectId")
    suspend fun updateSecondaryLivelihoodForSubject(
        subjectId: Int,
        userId: String,
        secondaryLivelihoodId: Int
    )

    @Query("SELECT COUNT(*) from $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME where subjectId = :subjectId and userId = :userId")
    suspend fun isSubjectLivelihoodMappingAvailable(subjectId: Int, userId: String): Int

    @Query("SELECT * from $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME where subjectId = :subjectId and userId = :userId")
    suspend fun getSubjectLivelihoodMappingAvailable(
        subjectId: Int,
        userId: String
    ): SubjectLivelihoodMappingEntity?

    @Transaction
    suspend  fun insertOrModifyLivelihoodMapping(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity) {
        if (isSubjectLivelihoodMappingAvailable(
                subjectId =subjectLivelihoodMappingEntity.subjectId!! ,
            userId = subjectLivelihoodMappingEntity.userId

            ) == 0
        ) {
            insertSubjectLivelihoodMapping(subjectLivelihoodMappingEntity)
        } else {
            updatePrimaryLivelihoodForSubject(
                userId = subjectLivelihoodMappingEntity.userId ?: BLANK_STRING,
                subjectId = subjectLivelihoodMappingEntity.subjectId,
                primaryLivelihoodId = subjectLivelihoodMappingEntity.primaryLivelihoodId)
            updateSecondaryLivelihoodForSubject( userId = subjectLivelihoodMappingEntity.userId ?: BLANK_STRING,
                subjectId = subjectLivelihoodMappingEntity.subjectId,
                secondaryLivelihoodId = subjectLivelihoodMappingEntity.secondaryLivelihoodId)
        }
    }

}