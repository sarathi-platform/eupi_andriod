package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.model.response.AnswersItem
import com.patsurvey.nudge.model.response.PATSummaryResponseItem
import com.patsurvey.nudge.utils.ANSWER_TABLE
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DOUBLE_ZERO
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.formatRatio
import com.patsurvey.nudge.utils.stringToDouble

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


) {

    companion object {
        fun getSectionAnswerEntity(
            patSummaryResponseItem: PATSummaryResponseItem,
            answersItem: AnswersItem?,
            quesDetails: QuestionEntity
        ): SectionAnswerEntity {
            val selectedAnswerEntity = SectionAnswerEntity(
                id = 0,
                optionId = 0,
                didiId = patSummaryResponseItem.beneficiaryId
                    ?: 0,
                questionId = answersItem?.questionId
                    ?: 0,
                villageId = patSummaryResponseItem.villageId
                    ?: 0,
                actionType = answersItem?.section
                    ?: TYPE_EXCLUSION,
                weight = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                    0
                )?.weight) else 0,
                summary = answersItem?.summary,
                optionValue = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                    0
                )?.optionValue) else 0,
                totalAssetAmount = if (quesDetails?.questionFlag.equals(
                        QUESTION_FLAG_WEIGHT
                    )
                ) answersItem?.totalWeight?.toDouble() else stringToDouble(
                    formatRatio(answersItem?.ratio ?: DOUBLE_ZERO)
                ),
                needsToPost = false,
                answerValue = (if (quesDetails?.questionFlag.equals(
                        QUESTION_FLAG_WEIGHT
                    )
                ) answersItem?.totalWeight?.toDouble() else stringToDouble(
                    formatRatio(answersItem?.ratio ?: DOUBLE_ZERO)
                )).toString(),
                type = answersItem?.questionType
                    ?: QuestionType.RadioButton.name,
                assetAmount = answersItem?.assetAmount
                    ?: "0",
                questionFlag = quesDetails?.questionFlag
                    ?: BLANK_STRING
            )
            return selectedAnswerEntity
        }

        fun getSectionAnswerEntity(
            answersItem: AnswersItem?,
            item: PATSummaryResponseItem,
            quesDetails: QuestionEntity
        ) = SectionAnswerEntity(
            id = 0,
            optionId = answersItem?.options?.get(
                0
            )?.optionId ?: 0,
            didiId = item.beneficiaryId
                ?: 0,
            questionId = answersItem?.questionId
                ?: 0,
            villageId = item.villageId
                ?: 0,
            actionType = answersItem?.section
                ?: TYPE_EXCLUSION,
            weight = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                0
            )?.weight) else 0,
            summary = answersItem?.summary,
            optionValue = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                0
            )?.optionValue) else 0,
            totalAssetAmount = if (quesDetails?.questionFlag.equals(
                    QUESTION_FLAG_WEIGHT
                )
            ) answersItem?.totalWeight?.toDouble() else stringToDouble(
                formatRatio(answersItem?.ratio ?: DOUBLE_ZERO)
            ),
            needsToPost = false,
            answerValue = if (answersItem?.options?.isNotEmpty() == true) (answersItem?.options?.get(
                0
            )?.display
                ?: BLANK_STRING) else BLANK_STRING,
            type = answersItem?.questionType
                ?: QuestionType.RadioButton.name
        )
    }

}
