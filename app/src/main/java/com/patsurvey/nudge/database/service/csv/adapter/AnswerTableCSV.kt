package com.patsurvey.nudge.database.service.csv.adapter

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.opencsv.bean.CsvBindByName
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.service.csv.Exportable
import com.patsurvey.nudge.utils.BLANK_STRING

data class AnswerTableCSV(
    @CsvBindByName(column = "id")
    var id: Int,

    @CsvBindByName(column = "optionId")
    val optionId: Int?=1,

    @CsvBindByName(column = "questionId")
    var questionId : Int,

    @CsvBindByName(column = "didiId")
    var didiId : Int,

    @CsvBindByName(column = "villageId")
    var villageId : Int,

    @CsvBindByName(column = "actionType")
    var actionType : String,

    @CsvBindByName(column = "type")
    var type : String,

    @CsvBindByName(column = "answerValue")
    var answerValue : String,

    @CsvBindByName(column = "weight")
    val weight: Int? = 0,

    @CsvBindByName(column = "optionValue")
    val optionValue: Int? = -1,

    @CsvBindByName(column = "totalAssetAmount")
    val totalAssetAmount: Double? = 0.0,

    @CsvBindByName(column = "summary")
    val summary: String? = BLANK_STRING,

    @CsvBindByName(column = "needsToPost")
    var needsToPost: Boolean = true,

    @CsvBindByName(column = "assetAmount")
    val assetAmount: String? = BLANK_STRING,

    @CsvBindByName(column = "questionImageUrl")
    var questionImageUrl: String? = BLANK_STRING,

    @CsvBindByName(column = "questionFlag")
    var questionFlag: String? = BLANK_STRING
): Exportable

    fun List<SectionAnswerEntity>.toCsv() = map {
        AnswerTableCSV(
            id = it.id,
            optionId = it.optionId,
            questionId = it.questionId,
            didiId = it.didiId,
            villageId = it.villageId,
            actionType = it.actionType,
            type = it.type,
            answerValue = it.answerValue,
            weight = it.weight,
            optionValue = it.optionValue,
            totalAssetAmount = it.totalAssetAmount,
            summary = it.summary,
            needsToPost = it.needsToPost,
            assetAmount = it.assetAmount,
            questionImageUrl = it.questionImageUrl,
            questionFlag = it.questionFlag
        )
    }