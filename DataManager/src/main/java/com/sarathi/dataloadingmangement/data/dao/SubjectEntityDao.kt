package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.SUBJECT_TABLE
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel

@Dao
interface SubjectEntityDao {

    @Insert
    fun addSubject(subjectEntity: SubjectEntity)

    @Insert
    fun addAllSubjects(subjectEntityList: List<SubjectEntity>)

    @Query("SELECT * from $SUBJECT_TABLE where subjectId = :subjectId and userId = :userId")
    suspend fun getSubjectForId(subjectId: Int, userId: String): SubjectEntity

    @Query("SELECT * from $SUBJECT_TABLE where userId = :userId")
    fun getAllSubjects(userId: String): List<SubjectEntity>

    @Query("SELECT * from $SUBJECT_TABLE where userId = :userId and subjectId in (:subjectIds)")
    suspend fun getAllSubjectForIds(userId: String, subjectIds: List<Int>): List<SubjectEntity>

    @Query("SELECT count(*) from $SUBJECT_TABLE where userId = :userId")
    fun getCountForSubject(userId: String): Int

    @Query("SELECT count(*) from $SUBJECT_TABLE where subjectId = :subjectId and userId = :userId")
    fun isSubjectPresentInDb(userId: String, subjectId: Int): Int

    @Query("SELECT crpImageName from $SUBJECT_TABLE where userId = :userId")
    fun getDidiImageUrlForSmallGroup(userId: String): List<String>

    @Query("DELETE from $SUBJECT_TABLE where userId = :userId")
    fun deleteSubjectsForUsers(userId: String)

    @Query("SELECT  subject_table.subjectId, subject_table.subjectName,subject_table.crpImageName, subject_table.dadaName, subject_table.cohortId, subject_table.cohortName, subject_table.houseNo, subject_table.villageId, subject_table.villageName, subject_table.crpImageLocalPath, subject_table.voName, subject_livelihood_mapping_table.livelihoodId from subject_table inner join subject_livelihood_mapping_table on subject_table.subjectId = subject_livelihood_mapping_table.subjectId where subject_table.userId = :userId and subject_livelihood_mapping_table.userId = :userId and subject_livelihood_mapping_table.livelihoodId!=-1")
    fun getSubjectEntityWithLivelihoodMappingUiModelList(userId: String): List<SubjectEntityWithLivelihoodMappingUiModel>

}

