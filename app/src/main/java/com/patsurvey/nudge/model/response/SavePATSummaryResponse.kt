package com.patsurvey.nudge.model.response

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.BLANK_STRING

data class SavePATSummaryResponse(

	@SerializedName("PATSummaryResponse")
	val pATSummaryResponse: List<PATSummaryResponseItem?>? = null
)

data class SummaryOptionsItem(

	@SerializedName("summary")
	val summary: String? = null,

	@SerializedName("optionValue")
	val optionValue: Int? = null,

	@SerializedName("display")
	val display: String? = null,

	@SerializedName("count")
	val count: Int? = null,

	@SerializedName("weight")
	val weight: Int? = null,

	@SerializedName("optionId")
	val optionId: Int? = null,

	@SerializedName("selected")
	val selected: Boolean? = null
)

data class AnswersItem(

	@SerializedName("summary")
	val summary: String? = BLANK_STRING,

	@SerializedName("score")
	val score: Int? = 0,

	@SerializedName("questionId")
	val questionId: Int? = 0,

	@SerializedName("options")
	val options: List<SummaryOptionsItem?>? = null,

	@SerializedName("totalWeight")
	val totalWeight: Int? = 0,

	@SerializedName("section")
	val section: String? = BLANK_STRING,

	@SerializedName("displayQuestion")
	val displayQuestion: String? = BLANK_STRING,

	@SerializedName("questionType")
	val questionType: String? = BLANK_STRING,

	@SerializedName("assetAmount")
	val assetAmount: String? = "0",

)

data class 	PATSummaryResponseItem(

	@SerializedName("surveyId")
	val surveyId: Int? = null,

	@SerializedName("stateId")
	val stateId: Int? = null,

	@SerializedName("answers")
	val answers: List<AnswersItem?>? = null,

	@SerializedName("totalScore")
	val totalScore: Int? = null,

	@SerializedName("createdDate")
	val createdDate: Any? = null,

	@SerializedName("createdBy")
	val createdBy: Int? = null,

	@SerializedName("modifiedDate")
	val modifiedDate: Any? = null,

	@SerializedName("modifiedBy")
	val modifiedBy: Int? = null,

	@SerializedName("id")
	val id: String? = null,

	@SerializedName("userType")
	val userType: String? = null,

	@SerializedName("villageId")
	val villageId: Int? = 0,

	@SerializedName("beneficiaryId")
	val beneficiaryId: Int? = 0,

	@SerializedName("status")
	val status: Int? = null,

	@SerializedName("patSurveyStatus")
	val patSurveyStatus: Int? = 0,

	@SerializedName("section1Status")
	val section1Status: Int? = 0,

	@SerializedName("section2Status")
	val section2Status: Int? = 0,

	@SerializedName("shgFlag")
	val shgFlag: Int? = -1


)


