package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.SURVEY_TABLE
import com.sarathi.dataloadingmangement.model.survey.response.SurveyResponseModel

@Entity(tableName = SURVEY_TABLE)
data class SurveyEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    var userId: String? = BLANK_STRING,
    @SerializedName("surveyId")
    @Expose
    @ColumnInfo(name = "surveyId")
    val surveyId: Int,

    @SerializedName("surveyName")
    @Expose
    @ColumnInfo(name = "surveyName")

    val surveyName: String,

    @SerializedName("surveyPassingMark")
    @Expose
    @ColumnInfo(name = "surveyPassingMark")

    val surveyPassingMark: Int,

    @SerializedName("thresholdScore")
    @Expose
    @ColumnInfo(name = "thresholdScore")
    val thresholdScore: Int,

    @SerializedName("referenceId")
    @Expose
    @ColumnInfo(name = "referenceId")
    val referenceId: Int
) {

    companion object {
        fun getSurveyEntity(
            userId: String,
            surveyApiResponseModel: SurveyResponseModel
        ): SurveyEntity {
            return SurveyEntity(
                userId = userId,
                id = 0,
                surveyId = surveyApiResponseModel.surveyId,
                surveyName = surveyApiResponseModel.originalValue ?: BLANK_STRING,
                surveyPassingMark = surveyApiResponseModel.surveyPassingMark,
                thresholdScore = surveyApiResponseModel.thresholdScore,
                referenceId = surveyApiResponseModel.referenceId
            )
        }
    }
}
