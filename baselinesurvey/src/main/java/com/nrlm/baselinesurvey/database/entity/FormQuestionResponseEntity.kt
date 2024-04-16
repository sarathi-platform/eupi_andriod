package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nrlm.baselinesurvey.FORM_QUESTION_RESPONSE_TABLE
import com.nudge.syncmanager.BLANK_STRING

@Entity(tableName = FORM_QUESTION_RESPONSE_TABLE)
data class FormQuestionResponseEntity(
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

    @ColumnInfo(name = "selectedValue")
    var selectedValue: String,

    @ColumnInfo(name = "referenceId")
    val referenceId: String
)