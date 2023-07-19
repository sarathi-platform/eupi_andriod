package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.ANSWER_TABLE
import com.patsurvey.nudge.utils.BLANK_STRING

@Entity(tableName = ANSWER_TABLE)
data class SectionAnswerEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "optionId")
    val optionId: Int?=1,

    @ColumnInfo(name = "questionId")
    var questionId : Int,

    @ColumnInfo(name = "didiId")
    var didiId : Int,

    @ColumnInfo(name = "villageId")
    var villageId : Int,

    @ColumnInfo(name = "actionType")
    var actionType : String,

    @ColumnInfo(name = "type")
    var type : String,

    @ColumnInfo(name = "answerValue")
    var answerValue : String,

    @SerializedName("weight")
    val weight: Int? = 0,

    @SerializedName("optionValue")
    val optionValue: Int? = -1,

    @SerializedName("totalAssetAmount")
    val totalAssetAmount: Double? = 0.0,

    @SerializedName("summary")
    val summary: String? = BLANK_STRING,

    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true,

    @SerializedName("assetAmount")
    val assetAmount: String? = BLANK_STRING,

    @SerializedName("questionImageUrl")
    var questionImageUrl: String? = BLANK_STRING,

    @ColumnInfo(name = "questionFlag")
    var questionFlag: String? = BLANK_STRING



)
