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

    @Query("select *from form_table where userId =:userId and taskid=:taskId ")
    fun getFormDataForTask(
        userId: String,
        taskId: Int,

        ): List<FormEntity>?
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

    @Query(
        "select form_table.taskid,form_table.activityId,form_table.localReferenceId,form_table.formGenerateDate," +
                "form_table.userId, form_table.missionId,form_table.subjectid, form_table.createdDate, form_table.formType,form_table.taskid," +
                " form_table.surveyId,form_table.subjectType,form_table.id,form_table.isFormGenerated from form_table inner join task_table on form_table.taskid=task_table.taskId where form_table.userId =:userId and form_table.isFormGenerated=:isFormGenerated and form_table.activityId=:activityId  and task_table.userId=:userId  order by createdDate DESC"
    )
    suspend fun getFormSummaryData(
        userId: String,
        activityId: Int,
        isFormGenerated: Boolean,
    ): List<FormEntity>

    @Query(
        "select form_table.taskid,form_table.activityId,form_table.localReferenceId,form_table.formGenerateDate," +
                "form_table.userId, form_table.missionId,form_table.subjectid, form_table.createdDate, form_table.formType,form_table.taskid," +
                " form_table.surveyId,form_table.subjectType,form_table.id ,form_table.subjectType,form_table.isFormGenerated from form_table inner join task_table on form_table.taskid=task_table.taskId where form_table.userId =:userId and form_table.activityId=:activityId  and task_table.userId=:userId  order by createdDate DESC"
    )
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

    @Query("select * from form_table where userId =:userId")
    suspend fun getAllFormSummaryDataForUser(
        userId: String,
    ): List<FormEntity>


    @Query("Delete from form_table where userId =:userId ")
    fun deleteFormForUser(
        userId: String,
    ): Int


}