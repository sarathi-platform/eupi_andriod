package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity

@Dao
interface SubjectLivelihoodMappingDao {

    @Insert
    suspend fun insertSubjectLivelihoodMapping(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSubjectLivelihoodMapping(subjectLivelihoodMappingEntity: List<SubjectLivelihoodMappingEntity>)

    @Query("UPDATE $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME set  status =:status where userId = :userId and type=:type and subjectId = :subjectId")
    suspend fun softDeleteLivelihoodForSubject(
        subjectId: Int,
        userId: String,
        status:Int,
        type: Int
    )

    @Query("SELECT COUNT(*) from $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME where subjectId = :subjectId and type = :type and userId = :userId" )
    suspend fun isSubjectLivelihoodMappingAvailable(subjectId: Int, userId: String,type:Int): Int

    @Query("SELECT * from $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME where subjectId = :subjectId  and userId = :userId  and type IN (:type) And status =1")
    suspend fun getSubjectLivelihoodMappingAvailable(
        subjectId: Int,
        userId: String,
        type: List<Int>
        ): List<SubjectLivelihoodMappingEntity>

    @Query("DELETE from $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME where userId = :userId")
    fun deleteSubjectLivelihoodMappingForUser(userId: String)

    @Query("SELECT * from $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME" +
            " where subjectId IN (:subjectIds) and userId = :userId")
    suspend fun getSubjectsLivelihoodMapping(
        subjectIds: List<Int>,
        userId: String
    ): List<SubjectLivelihoodMappingEntity>

    @Query("DELETE from $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME where userId = :userId")
    fun deleteLivelihoodSubjectsForUsers(userId: String)

    @Query("SELECT * from $SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME where subjectId = :subjectId and userId = :userId")
    suspend fun getSubjectLivelihoodMapping(
        subjectId: Int,
        userId: String
    ): SubjectLivelihoodMappingEntity?
}