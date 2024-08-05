package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity

@Dao
interface SubjectLivelihoodMappingDao {

    @Insert
    fun insertSubjectLivelihoodMapping(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity)

    @Query("UPDATE $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME set primaryLivelihoodId = :primaryLivelihoodId where userId = :userId and subjectId = :subjectId")
    fun updatePrimaryLivelihoodForSubject(subjectId: Int, userId: String, primaryLivelihoodId: Int)

    @Query("UPDATE $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME set secondaryLivelihoodId = :secondaryLivelihoodId where userId = :userId and subjectId = :subjectId")
    fun updateSecondaryLivelihoodForSubject(
        subjectId: Int,
        userId: String,
        secondaryLivelihoodId: Int
    )

    @Query("SELECT COUNT(*) from $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME where subjectId = :subjectId and userId = :userId")
    fun isSubjectLivelihoodMappingAvailable(subjectId: Int, userId: String): Int


}