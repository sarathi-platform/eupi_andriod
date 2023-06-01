package com.patsurvey.nudge.model.request

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.USER_CRP

data class PATSummarySaveRequest(
    @SerializedName("surveyId")
    val surveyId: Int? = 0,

    @SerializedName("stateId")
    val stateId: Int? = 0,

    @SerializedName("languageId")
    val languageId: Int? = 0,

    @SerializedName("answerDetailDTOList")
    val answerDetailDTOList: List<AnswerDetailDTOListItem?>? = emptyList(),

    @SerializedName("userType")
    val userType: String? = BLANK_STRING,

    @SerializedName("totalScore")
    val totalScore: Int? = 0,

    @SerializedName("villageId")
    val villageId: Int? = 0,

    @SerializedName("beneficiaryId")
    val beneficiaryId: Int? = 0,

    @ColumnInfo(name = "patSurveyStatus")
    var patSurveyStatus: Int=0,

    @ColumnInfo(name = "section1Status")
    var section1Status: Int=0,

    @ColumnInfo(name = "section2Status")
    var section2Status: Int=0
)

data class AnswerDetailDTOListItem(

    @SerializedName("questionId")
    val questionId: Int? = 0,

    @SerializedName("options")
    val options: List<OptionsItem?>? = emptyList(),

    @SerializedName("section")
    val section: String? = BLANK_STRING,

    )