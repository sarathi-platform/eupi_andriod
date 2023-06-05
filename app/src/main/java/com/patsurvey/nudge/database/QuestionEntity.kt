package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.QUESTION_TABLE

@Entity(tableName = QUESTION_TABLE)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "questionId")
    var questionId: Int? = 0,

    @ColumnInfo(name = "options")
    var options: List<OptionsItem> = emptyList(),

    @ColumnInfo(name = "questionImageUrl")
    var questionImageUrl: String? = BLANK_STRING,

    @ColumnInfo(name = "type")
    var type: String? = BLANK_STRING,

    @ColumnInfo(name = "questionDisplay")
    var questionDisplay: String? = BLANK_STRING,

    @ColumnInfo(name = "questionSummary")
    var questionSummary: String? = BLANK_STRING,

    @ColumnInfo(name = "gotoQuestionId")
    var gotoQuestionId: Int? = 0,

    @ColumnInfo(name = "order")
    var order: Int? = 0,

    @ColumnInfo(name = "questionFlag")
    var questionFlag: String? = BLANK_STRING,

    @ColumnInfo(name = "json")
    var json: String? = BLANK_STRING,

    @ColumnInfo(name = "actionType")
    var actionType: String? = BLANK_STRING,

    @ColumnInfo(name = "orderNumber")
    var sectionOrderNumber: Int? = 0,

    @ColumnInfo(name = "languageId")
    var languageId: Int? = 1,

    @ColumnInfo(name = "totalAssetAmount")
    var totalAssetAmount: Int? = 0,

    @ColumnInfo(name = "surveyId")
    var surveyId: Int? = 0,

    @ColumnInfo(name = "surveyPassingMark")
    var surveyPassingMark: Int? = 0,

    @ColumnInfo(name = "thresholdScore")
    var thresholdScore: Int? = 0,
)















