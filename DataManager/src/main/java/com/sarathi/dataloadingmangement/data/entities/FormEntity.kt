package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.toDate
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.FORM_TABLE_NAME
import java.util.Date

@Entity(tableName = FORM_TABLE_NAME)
data class FormEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var taskid: Int,
    var subjectid: Int,
    @TypeConverters(DateConverter::class)
    val createdDate: Date = System.currentTimeMillis().toDate(),
    var formGenerateDate: String,
    var subjectType: String,
    var formType: String,
    var surveyId: Int,
    var userId: String? = BLANK_STRING,
    var isFormGenerated: Boolean,
    var localReferenceId: String,
    var missionId: Int,
    var activityId: Int
) {
    companion object {
        fun getFormEntity(
            userId: String,
            referenceId: String,
            taskId: Int,
            subjectType: String,
            subjectId: Int,
            surveyId: Int,
            missionId: Int,
            activityId: Int
        ): FormEntity {
            return FormEntity(
                id = 0,
                taskid = taskId,
                subjectid = subjectId,
                subjectType = subjectType,
                formType = "Form_E",
                surveyId = surveyId,
                userId = userId,
                missionId = missionId,
                activityId = activityId,
                formGenerateDate = BLANK_STRING,
                isFormGenerated = false,
                localReferenceId = referenceId,
                createdDate = System.currentTimeMillis().toDate()
            )
        }

    }


}
