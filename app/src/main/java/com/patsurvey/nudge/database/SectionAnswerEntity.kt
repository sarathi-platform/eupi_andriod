package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.ANSWER_TABLE
import com.patsurvey.nudge.utils.BLANK_STRING

@Entity(tableName = ANSWER_TABLE)
data class SectionAnswerEntity(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("optionId")
    @Expose
    @ColumnInfo(name = "optionId")
    val optionId: Int?=1,

    @SerializedName("questionId")
    @Expose
    @ColumnInfo(name = "questionId")
    var questionId : Int,

    @SerializedName("didiId")
    @Expose
    @ColumnInfo(name = "didiId")
    var didiId : Int,

    @SerializedName("villageId")
    @Expose
    @ColumnInfo(name = "villageId")
    var villageId : Int,

    @SerializedName("actionType")
    @Expose
    @ColumnInfo(name = "actionType")
    var actionType : String,

    @SerializedName("type")
    @Expose
    @ColumnInfo(name = "type")
    var type : String,

    @SerializedName("answerValue")
    @Expose
    @ColumnInfo(name = "answerValue")
    var answerValue : String,

    @SerializedName("weight")
    @Expose
    val weight: Int? = 0,

    @SerializedName("optionValue")
    @Expose
    val optionValue: Int? = -1,

    @SerializedName("totalAssetAmount")
    @Expose
    val totalAssetAmount: Double? = 0.0,

    @SerializedName("summary")
    @Expose
    val summary: String? = BLANK_STRING,

    @SerializedName("needsToPost")
    @Expose
    @ColumnInfo(name = "needsToPost")
    var needsToPost: Boolean = true,

    @SerializedName("assetAmount")
    @Expose
    val assetAmount: String? = BLANK_STRING,

    @SerializedName("questionImageUrl")
    @Expose
    var questionImageUrl: String? = BLANK_STRING,

    @SerializedName("questionFlag")
    @Expose
    @ColumnInfo(name = "questionFlag")
    var questionFlag: String? = BLANK_STRING



)
