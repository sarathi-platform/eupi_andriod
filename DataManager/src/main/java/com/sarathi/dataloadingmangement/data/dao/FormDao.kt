package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.FormEntity

@Dao
interface FormDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForm(formEntity: FormEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFormDetail(formEntity: List<FormEntity>)


    @Query("select count(*) from form_table where userId =:userId and subjectId=:subjectId and taskid=:taskId  and localReferenceId=:localReferenceId")
    fun getFormData(
        userId: String,
        taskId: Int,
        subjectId: Int,
        localReferenceId: String
    ): Int

    @Transaction
    fun insertFormData(formEntity: FormEntity) {
        if (getFormData(
                userId = formEntity.userId ?: BLANK_STRING,
                subjectId = formEntity.subjectid,
                taskId = formEntity.taskid,
                localReferenceId = formEntity.localReferenceId
            ) == 0
        ) {
            insertForm(formEntity)
        }
    }

    @Query("Delete from form_table where userId =:userId and taskId=:taskId and localReferenceId =:referenceId")
    fun deleteForm(
        userId: String,
        referenceId: String,
        taskId: Int
    ): Int

    @Query("select * from form_table where userId =:userId and isFormGenerated=:isFormGenerated and activityId=:activityId order by createdDate DESC")
    suspend fun getFormSummaryData(
        userId: String,
        activityId: Int,
        isFormGenerated: Boolean
    ): List<FormEntity>

    @Query("select * from form_table where userId =:userId and activityId=:activityId order by createdDate DESC")
    suspend fun getAllFormSummaryData(
        userId: String,
        activityId: Int,
    ): List<FormEntity>

    @Query("Update  form_table  set isFormGenerated=:isFormGenerated , formGenerateDate=:generatedDate where userId =:userId and localReferenceId=:localReferenceId")
    suspend fun updateFormData(
        userId: String,
        generatedDate: String,
        localReferenceId: String,
        isFormGenerated: Boolean
    )

    @Query("Delete from form_table where userId =:userId ")
    fun deleteFormForUser(
        userId: String,
    ): Int


}