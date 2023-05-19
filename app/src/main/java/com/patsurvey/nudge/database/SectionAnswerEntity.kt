package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.utils.ANSWER_TABLE
import com.patsurvey.nudge.utils.CASTE_TABLE

@Entity(tableName = ANSWER_TABLE)
data class SectionAnswerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "answerId")
    val answerId: Int?=1,

    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "questionId")
    var questionId : Int,

    @ColumnInfo(name = "didiId")
    var didiId : Int,

    @ColumnInfo(name = "actionType")
    var actionType : String,

    @ColumnInfo(name = "type")
    var type : String,

    @ColumnInfo(name = "answerOption")
    var answerOption : String,

    @ColumnInfo(name = "answerValue")
    var answerValue : String


)
