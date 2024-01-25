package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.SURVEY_TABLE

@Entity(tableName = SURVEY_TABLE)
data class SurveyEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

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

    @SerializedName("languageId")
    @Expose
    @ColumnInfo(name = "languageId")
    val languageId: Int,

)
