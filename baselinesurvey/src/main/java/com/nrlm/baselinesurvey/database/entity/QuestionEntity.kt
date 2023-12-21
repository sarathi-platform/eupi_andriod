package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.QUESTION_TABLE
import com.nrlm.baselinesurvey.database.converters.QuestionsOptionsConverter
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem

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

    @SerializedName("sectionId")
    @Expose
    @ColumnInfo(name = "sectionId")
    var sectionId: Int = 0,

    @SerializedName("surveyId")
    @Expose
    @ColumnInfo(name = "surveyId")
    val surveyId: Int,

    @SerializedName("options")
    @Expose
    @ColumnInfo(name = "options")
    @TypeConverters(QuestionsOptionsConverter::class)
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

    /* @SerializedName("questionFlag")
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
    var sectionOrderNumber: Int? = 0,*/

    @SerializedName("languageId")
    @Expose
    @ColumnInfo(name = "languageId")
    var languageId: Int? = 1,
)
