package com.patsurvey.nudge.model.request

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.QuestionType

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
    val totalScore: Double? = 0.0,

    @SerializedName("villageId")
    val villageId: Int? = 0,

    @SerializedName("beneficiaryId")
    val beneficiaryId: Int? = 0,

    @SerializedName("beneficiaryName")
    val beneficiaryName: String? = BLANK_STRING,

    @SerializedName("patSurveyStatus")
    var patSurveyStatus: Int = 0,

    @SerializedName("section1Status")
    var section1Status: Int = 0,

    @SerializedName("section2Status")
    var section2Status: Int = 0,

    @SerializedName("patExclusionStatus")
    var patExclusionStatus: Int = 0,

    @SerializedName("shgFlag")
    val shgFlag: Int? = -1,
    @SerializedName("beneficiaryAddress")
    val beneficiaryAddress: String? = BLANK_STRING,

    @SerializedName("guardianName")
    val guardianName: String? = BLANK_STRING,

    @SerializedName("cohortName")
    val cohortName: String? = BLANK_STRING,
    @SerializedName("externalSystemId")
    val externalSystemId: String? = BLANK_STRING
) {

    companion object {
        fun getPatSummarySaveRequest(
            didiEntity: DidiEntity,
            answerDetailDTOList: List<AnswerDetailDTOListItem?>,
            villageEntity: VillageEntity,
            surveyId: Int,
            languageId: Int,
            userType: String
        ): PATSummarySaveRequest {
            return PATSummarySaveRequest(
                surveyId = surveyId,
                stateId = villageEntity.stateId,
                languageId = languageId,
                userType = userType,
                totalScore = didiEntity.score,
                villageId = didiEntity.villageId,
                beneficiaryId = didiEntity.serverId,
                beneficiaryName = didiEntity.name,
                patSurveyStatus = didiEntity.patSurveyStatus,
                section1Status = didiEntity.section1Status,
                section2Status = didiEntity.section2Status,
                patExclusionStatus = didiEntity.patExclusionStatus,
                shgFlag = didiEntity.shgFlag,
                beneficiaryAddress = didiEntity.address,
                cohortName = didiEntity.cohortName,
                guardianName = didiEntity.guardianName,
                answerDetailDTOList = answerDetailDTOList
            )
        }
    }

    fun toJson(): JsonObject {
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
        jsonObject.addProperty("patExclusionStatus", patExclusionStatus)
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

) {

    companion object {
        fun getAnswerDetailDtoListItem(
            sectionAnswerEntityList: List<SectionAnswerEntity>,
            numericAnswerEntityList: List<NumericAnswerEntity>
        ): List<AnswerDetailDTOListItem> {
            var optionList: List<OptionsItem> = emptyList()
            val qList = mutableListOf<AnswerDetailDTOListItem>()
            if (sectionAnswerEntityList.isNotEmpty()) {
                sectionAnswerEntityList.forEach { sectionAnswerEntity ->
                    if (!sectionAnswerEntity.type.equals(QuestionType.Numeric_Field.name, true)) {
                        optionList = listOf(
                            OptionsItem(
                                optionId = sectionAnswerEntity.optionId,
                                optionValue = sectionAnswerEntity.optionValue,
                                count = 0,
                                summary = sectionAnswerEntity.summary,
                                display = sectionAnswerEntity.answerValue,
                                weight = sectionAnswerEntity.weight,
                                isSelected = false
                            )
                        )
                    } else {
                        val numOptionList =
                            numericAnswerEntityList.filter { it.questionId == sectionAnswerEntity.questionId }

                        val tList: java.util.ArrayList<OptionsItem> = arrayListOf()
                        if (numOptionList.isNotEmpty()) {
                            numOptionList.forEach { numOption ->
                                tList.add(
                                    OptionsItem(
                                        optionId = numOption.optionId,
                                        optionValue = numOption.optionValue,
                                        count = numOption.count,
                                        summary = sectionAnswerEntity.summary,
                                        display = sectionAnswerEntity.answerValue,
                                        weight = numOption.weight,
                                        isSelected = false
                                    )
                                )
                            }
                            optionList = tList
                        } else {
                            tList.add(
                                OptionsItem(
                                    optionId = sectionAnswerEntity.optionId,
                                    optionValue = 0,
                                    count = 0,
                                    summary = sectionAnswerEntity.summary,
                                    display = sectionAnswerEntity.answerValue,
                                    weight = sectionAnswerEntity.weight,
                                    isSelected = false
                                )
                            )

                            optionList = tList
                        }

                    }
                    qList.add(
                        AnswerDetailDTOListItem(
                            questionId = sectionAnswerEntity.questionId,
                            section = sectionAnswerEntity.actionType,
                            options = optionList,
                            assetAmount = sectionAnswerEntity.assetAmount
                        )
                    )
                }
            }
            return qList
        }
    }

}