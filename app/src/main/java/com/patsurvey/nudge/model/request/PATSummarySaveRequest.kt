package com.patsurvey.nudge.model.request

import androidx.room.ColumnInfo
import com.google.gson.JsonObject
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

    @SerializedName("beneficiaryName")
    val beneficiaryName: String? = BLANK_STRING,

    @ColumnInfo(name = "patSurveyStatus")
    var patSurveyStatus: Int=0,

    @ColumnInfo(name = "section1Status")
    var section1Status: Int=0,

    @ColumnInfo(name = "section2Status")
    var section2Status: Int=0,

    @SerializedName("shgFlag")
    val shgFlag: Int? = -1
) {
    fun toJson() : JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("surveyId", surveyId)
        jsonObject.addProperty("stateId", stateId)
        jsonObject.addProperty("languageId", languageId)
        jsonObject.addProperty("answerDetailDTOList", answerDetailDTOList.toString())
        jsonObject.addProperty("userType", userType)
        jsonObject.addProperty("totalScore", totalScore)
        jsonObject.addProperty("villageId", villageId)
        jsonObject.addProperty("beneficiaryId", beneficiaryId)
        jsonObject.addProperty("beneficiaryName", beneficiaryName)
        jsonObject.addProperty("patSurveyStatus", patSurveyStatus)
        jsonObject.addProperty("section1Status", section1Status)
        jsonObject.addProperty("section2Status", section2Status)
        jsonObject.addProperty("shgFlag", shgFlag)
        return jsonObject
    }
}

data class AnswerDetailDTOListItem(

    @SerializedName("questionId")
    val questionId: Int? = 0,

    @SerializedName("options")
    val options: List<OptionsItem?>? = emptyList(),

    @SerializedName("section")
    val section: String? = BLANK_STRING,

    @SerializedName("assetAmount")
    val assetAmount: String? = "0"

    )