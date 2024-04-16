package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.ANSWER_TABLE
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.database.converters.QuestionsOptionsConverter

@Entity(tableName = ANSWER_TABLE)
data class SectionAnswerEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,
    var userId: String? = BLANK_STRING,
    @SerializedName("questionId")
    @Expose
    @ColumnInfo(name = "questionId")
    var questionId : Int,

    @SerializedName("didiId")
    @Expose
    @ColumnInfo(name = "didiId")
    var didiId : Int,

    @SerializedName("surveyId")
    @Expose
    @ColumnInfo(name = "surveyId")
    var surveyId : Int,

    @SerializedName("sectionId")
    @Expose
    @ColumnInfo(name = "sectionId")
    var sectionId : Int,

    @SerializedName("questionType")
    @Expose
    @ColumnInfo(name = "questionType")
    var questionType : String,

    /*@SerializedName("answerValue")
    @Expose
    @ColumnInfo(name = "answerValue")
    var answerValue : String,*/

    @SerializedName("optionItems")
    @Expose
    @ColumnInfo("optionItems")
    @TypeConverters(QuestionsOptionsConverter::class)
    var optionItems: List<OptionItemEntity>,

    @SerializedName("questionSummary")
    @Expose
    val questionSummary: String? = BLANK_STRING,

    @SerializedName("needsToPost")
    @Expose
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true,
    /*
        @SerializedName("questionFlag")
        @Expose
        @ColumnInfo(name = "questionFlag")
        var questionFlag: String? = BLANK_STRING*/
)
