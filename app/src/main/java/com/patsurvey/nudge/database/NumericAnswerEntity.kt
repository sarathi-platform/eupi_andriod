package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.model.response.AnswersItem
import com.patsurvey.nudge.model.response.PATSummaryResponseItem
import com.patsurvey.nudge.model.response.SummaryOptionsItem
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.NUMERIC_TABLE_NAME

@Entity(tableName = NUMERIC_TABLE_NAME)
data class NumericAnswerEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("optionId")
    @Expose
    @ColumnInfo(name = "optionId")
    var optionId: Int,

    @SerializedName("questionId")
    @Expose
    @ColumnInfo(name = "questionId")
    var questionId: Int,

    @SerializedName("questionFlag")
    @Expose
    @ColumnInfo(name = "questionFlag")
    var questionFlag: String? = BLANK_STRING,

    @SerializedName("didiId")
    @Expose
    @ColumnInfo(name = "didiId")
    var didiId: Int,

    @SerializedName("weight")
    @Expose
    @ColumnInfo(name = "weight")
    var weight : Int,

    @SerializedName("count")
    @Expose
    @ColumnInfo(name = "count")
    val count : Int=0,

    @SerializedName("optionValue")
    @Expose
    @ColumnInfo(name = "optionValue")
    var optionValue: Int = 0

) {

    companion object {

        fun getNumericAnswerEntity(
            optionItem: SummaryOptionsItem?,
            answersItem: AnswersItem?,
            patSummaryResponseItem: PATSummaryResponseItem
        ): NumericAnswerEntity {
            val numericAnswerEntity = NumericAnswerEntity(
                id = 0,
                optionId = optionItem?.optionId
                    ?: 0,
                questionId = answersItem?.questionId
                    ?: 0,
                weight = optionItem?.weight
                    ?: 0,
                didiId = patSummaryResponseItem.beneficiaryId
                    ?: 0,
                count = optionItem?.count
                    ?: 0,
                optionValue = optionItem?.optionValue ?: 0
            )
            return numericAnswerEntity
        }

    }

}
