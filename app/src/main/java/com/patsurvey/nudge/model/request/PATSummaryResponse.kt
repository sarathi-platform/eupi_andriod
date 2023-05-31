package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName

data class PATSummaryResponse(

	@SerializedName("PATSummaryResponse")
	val pATSummaryResponse: List<PATSummaryResponseItem?>? = null
)

data class OptionsItem(

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
	val summary: String? = null,

	@SerializedName("score")
	val score: Int? = null,

	@SerializedName("questionId")
	val questionId: Int? = null,

	@SerializedName("options")
	val options: List<OptionsItem?>? = null,

	@SerializedName("totalWeight")
	val totalWeight: Any? = null,

	@SerializedName("section")
	val section: String? = null,

	@SerializedName("displayQuestion")
	val displayQuestion: String? = null,

	@SerializedName("questionType")
	val questionType: String? = null,

	@SerializedName("ratio")
	val ratio: Any? = null
)

data class PATSummaryResponseItem(

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
	val status: Int? = null
)
