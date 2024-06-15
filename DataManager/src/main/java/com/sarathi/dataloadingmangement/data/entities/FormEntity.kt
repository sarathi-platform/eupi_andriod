package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.FORM_TABLE_NAME

@Entity(tableName = FORM_TABLE_NAME)
data class FormEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var taskid: Int,
    var subjectid: Int,
    var formGenerateDate: String,
    var subjectType: String,
    var formType: String,
    var surveyId: Int,
    var userId: String? = BLANK_STRING,
    var isFormGenerated: Boolean,
    var localReferenceId: String,
) {
    companion object {
        fun getFormEntity(
            userId: String,
            referenceId: String,
            taskId: Int,
            subjectType: String,
            subjectId: Int,
            surveyId: Int
        ): FormEntity {
            return FormEntity(
                id = 0,
                taskid = taskId,
                subjectid = subjectId,
                subjectType = subjectType,
                formType = "Form_E",
                surveyId = surveyId,
                userId = userId,
                formGenerateDate = BLANK_STRING,
                isFormGenerated = false,
                localReferenceId = referenceId,
            )
        }

    }


}
