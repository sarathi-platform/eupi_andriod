package com.patsurvey.nudge.database.service.csv.adapter

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.opencsv.bean.CsvBindByName
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.service.csv.Exportable
import com.patsurvey.nudge.utils.BLANK_STRING

data class NumericAnswerTableCSV(
    @CsvBindByName(column = "id")
    var id: Int,

    @CsvBindByName(column = "optionId")
    var optionId: Int,

    @CsvBindByName(column = "questionId")
    var questionId: Int,

    @CsvBindByName(column = "questionFlag")
    var questionFlag: String? = BLANK_STRING,

    @CsvBindByName(column = "didiId")
    var didiId: Int,

    @CsvBindByName(column = "weight")
    var weight : Int,

    @CsvBindByName(column = "count")
    val count : Int=0,

    @CsvBindByName(column = "optionValue")
    var optionValue: Int =0,
): Exportable

fun List<NumericAnswerEntity>.toCsv() = map {
    NumericAnswerTableCSV(
        id = it.id,
        optionId = it.optionId,
        questionId = it.questionId,
        questionFlag = it.questionFlag,
        didiId = it.didiId,
        weight = it.weight,
        count = it.count,
        optionValue = it.optionValue
    )
}
