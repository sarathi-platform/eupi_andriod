package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nrlm.baselinesurvey.INPUT_TYPE_QUESTION_ANSWER_TABLE
import com.nudge.syncmanager.BLANK_STRING

@Entity(tableName = INPUT_TYPE_QUESTION_ANSWER_TABLE)
data class InputTypeQuestionAnswerEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var userId: String? = BLANK_STRING,

    @ColumnInfo(name = "didiId")
    val didiId: Int,

    @ColumnInfo(name = "surveyId")
    val surveyId: Int,

    @ColumnInfo(name = "sectionId")
    val sectionId: Int,

    @ColumnInfo(name = "questionId")
    val questionId: Int,

    @ColumnInfo(name = "optionId")
    val optionId: Int,

    @ColumnInfo(name = "inputValue")
    val inputValue: String,
)
