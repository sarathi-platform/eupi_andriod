package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.SUBJECT_TABLE
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity

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

}