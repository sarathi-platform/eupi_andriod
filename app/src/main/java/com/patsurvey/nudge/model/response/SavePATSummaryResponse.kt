package com.patsurvey.nudge.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.BLANK_STRING

data class SavePATSummaryResponse(

	@SerializedName("PATSummaryResponse")
	@Expose
	val pATSummaryResponse: List<PATSummaryResponseItem?>? = null
)

data class SummaryOptionsItem(

	@SerializedName("summary")
	@Expose
	val summary: String? = null,

	@SerializedName("optionValue")
	@Expose
	val optionValue: Int? = null,

	@SerializedName("display")
	@Expose
	val display: String? = null,

	@SerializedName("count")
	@Expose
	val count: Int? = null,

	@SerializedName("weight")
	@Expose
	val weight: Int? = null,

	@SerializedName("optionId")
	@Expose
	val optionId: Int? = null,

	@SerializedName("selected")
	@Expose
	val selected: Boolean? = null
)

data class AnswersItem(

	@SerializedName("summary")
	@Expose
	val summary: String? = BLANK_STRING,

	@SerializedName("score")
	@Expose
	val score: Int? = 0,

	@SerializedName("questionId")
	@Expose
	val questionId: Int? = 0,

	@SerializedName("options")
	@Expose
	val options: List<SummaryOptionsItem?>? = null,

	@SerializedName("totalWeight")
	@Expose
	val totalWeight: Int? = 0,

	@SerializedName("ratio")
	@Expose
	val ratio: String? = "0.0",


	@SerializedName("section")
	@Expose
	val section: String? = BLANK_STRING,

	@SerializedName("displayQuestion")
	@Expose
	val displayQuestion: String? = BLANK_STRING,

	@SerializedName("questionType")
	@Expose
	val questionType: String? = BLANK_STRING,

	@SerializedName("assetAmount")
	@Expose
	val assetAmount: String? = "0",

)

data class 	PATSummaryResponseItem(

	@SerializedName("surveyId")
	@Expose
	val surveyId: Int? = null,

	@SerializedName("stateId")
	@Expose
	val stateId: Int? = null,

	@SerializedName("answers")
	@Expose
	val answers: List<AnswersItem?>? = null,

	@SerializedName("totalScore")
	@Expose
	val totalScore: Int? = null,

	@SerializedName("createdDate")
	@Expose
	val createdDate: Any? = null,

	@SerializedName("createdBy")
	@Expose
	val createdBy: Int? = null,

	@SerializedName("modifiedDate")
	@Expose
	val modifiedDate: Any? = null,

	@SerializedName("modifiedBy")
	@Expose
	val modifiedBy: Int? = null,

	@SerializedName("id")
	@Expose
	val id: String? = null,

	@SerializedName("userType")
	@Expose
	val userType: String? = null,

	@SerializedName("villageId")
	@Expose
	val villageId: Int? = 0,

	@SerializedName("beneficiaryId")
	@Expose
	val beneficiaryId: Int? = 0,

	@SerializedName("status")
	@Expose
	val status: Int? = null,

	@SerializedName("patSurveyStatus")
	@Expose
	val patSurveyStatus: Int? = 0,

	@SerializedName("section1Status")
	@Expose
	val section1Status: Int? = 0,

	@SerializedName("section2Status")
	@Expose
	val section2Status: Int? = 0,

	@SerializedName("patExclusionStatus")
	@Expose
	val patExclusionStatus: Int? = 0,

	@SerializedName("shgFlag")
	@Expose
	val shgFlag: Int? = -1


)


