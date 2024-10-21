package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SURVEY_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.model.mat.response.SurveyConfigAttributeResponse

@Entity(tableName = SURVEY_CONFIG_TABLE_NAME)
data class SurveyConfigEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var key: String,
    var type: String,
    var tagId: Int,
    var value: String,
    var icon: String,
    var label: String,
    var componentType: String,
    var language: String,
    var activityId: Int,
    var missionId: Int,
    var formId: Int,
    var surveyId: Int,
    var userId: String
) {

    companion object {

        fun getSurveyConfigEntity(
            missionId: Int,
            activityId: Int,
            attributes: SurveyConfigAttributeResponse,
            userId: String,
            surveyId: Int
        ): SurveyConfigEntity {
            return SurveyConfigEntity(
                id = 0,
                key = attributes.key,
                type = attributes.type,
                value = attributes.value,
                componentType = attributes.componentType ?: BLANK_STRING,
                missionId = missionId,
                activityId = activityId,
                language = attributes.languageId ?: "en",
                userId = userId,
                label = attributes.label ?: BLANK_STRING,
                icon = attributes.icon ?: BLANK_STRING,
                formId = attributes.formId,
                surveyId = surveyId,
                tagId = attributes.tagId
            )
        }
    }

}
