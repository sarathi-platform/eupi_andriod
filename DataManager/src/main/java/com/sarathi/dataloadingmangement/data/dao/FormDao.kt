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

    @Query("select * from form_table where userId =:userId and isFormGenerated=:isFormGenerated order by createdDate DESC")
    suspend fun getFormSummaryData(
        userId: String,
        isFormGenerated: Boolean
    ): List<FormEntity>
}