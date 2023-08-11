package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.QUESTION_TABLE

@Entity(tableName = QUESTION_TABLE)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("questionId")
    @Expose
    @ColumnInfo(name = "questionId")
    var questionId: Int? = 0,

    @SerializedName("options")
    @Expose
    @ColumnInfo(name = "options")
    var options: List<OptionsItem> = emptyList(),

    @SerializedName("questionImageUrl")
    @Expose
    @ColumnInfo(name = "questionImageUrl")
    var questionImageUrl: String? = BLANK_STRING,

    @SerializedName("type")
    @Expose
    @ColumnInfo(name = "type")
    var type: String? = BLANK_STRING,

    @SerializedName("questionDisplay")
    @Expose
    @ColumnInfo(name = "questionDisplay")
    var questionDisplay: String? = BLANK_STRING,

    @SerializedName("questionSummary")
    @Expose
    @ColumnInfo(name = "questionSummary")
    var questionSummary: String? = BLANK_STRING,

    @SerializedName("gotoQuestionId")
    @Expose
    @ColumnInfo(name = "gotoQuestionId")
    var gotoQuestionId: Int? = 0,

    @SerializedName("order")
    @Expose
    @ColumnInfo(name = "order")
    var order: Int? = 0,

    @SerializedName("questionFlag")
    @Expose
    @ColumnInfo(name = "questionFlag")
    var questionFlag: String? = BLANK_STRING,

    @SerializedName("json")
    @Expose
    @ColumnInfo(name = "json")
    var json: String? = BLANK_STRING,

    @SerializedName("actionType")
    @Expose
    @ColumnInfo(name = "actionType")
    var actionType: String? = BLANK_STRING,

    @SerializedName("orderNumber")
    @Expose
    @ColumnInfo(name = "orderNumber")
    var sectionOrderNumber: Int? = 0,

    @SerializedName("languageId")
    @Expose
    @ColumnInfo(name = "languageId")
    var languageId: Int? = 1,

    @SerializedName("totalAssetAmount")
    @Expose
    @ColumnInfo(name = "totalAssetAmount")
    var totalAssetAmount: Int? = 0,

    @SerializedName("surveyId")
    @Expose
    @ColumnInfo(name = "surveyId")
    var surveyId: Int? = 0,

    @SerializedName("surveyPassingMark")
    @Expose
    @ColumnInfo(name = "surveyPassingMark")
    var surveyPassingMark: Int? = 0,

    @SerializedName("thresholdScore")
    @Expose
    @ColumnInfo(name = "thresholdScore")
    var thresholdScore: Int? = 0,

    @SerializedName("headingProductAssetValue")
    @Expose
    @ColumnInfo(name = "headingProductAssetValue")
    var headingProductAssetValue: String? = BLANK_STRING,
)















