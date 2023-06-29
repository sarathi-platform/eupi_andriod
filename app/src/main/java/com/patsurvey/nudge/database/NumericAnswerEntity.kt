package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.NUMERIC_TABLE_NAME

@Entity(tableName = NUMERIC_TABLE_NAME)
data class NumericAnswerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "optionId")
    var optionId: Int,

    @ColumnInfo(name = "questionId")
    var questionId: Int,

    @ColumnInfo(name = "questionFlag")
    var questionFlag: String? = BLANK_STRING,

    @ColumnInfo(name = "didiId")
    var didiId: Int,

    @ColumnInfo(name = "weight")
    var weight : Int,

    @ColumnInfo(name = "count")
    val count : Int=0,

    )
