package com.patsurvey.nudge.model.response

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.utils.BLANK_STRING

data class QuestionListResponse(

	@SerializedName("surveyId")
	val surveyId: Int? = null,

	 @SerializedName("surveyPassingMark")
	 val surveyPassingMark: Int,

	@SerializedName("thresholdScore")
	val thresholdScore: Int,

	@SerializedName("listOfQuestionSectionList")
	val listOfQuestionSectionList: List<ListOfQuestionSectionListItem?>? = null
)
data class ListOfQuestionSectionListItem(

	@SerializedName("actionType")
	val actionType: String? = null,

	@SerializedName("orderNumber")
	val orderNumber: Int? = 0,

	@SerializedName("questionList")
	val questionList: List<QuestionEntity?>? = null
)


data class OptionsItem(

	@SerializedName("display")
	val display: String? = null,

	@SerializedName("weight")
	val weight: Int? = null,

	@SerializedName("optionId")
	val optionId: Int? = null,

	@SerializedName("optionValue")
	val optionValue: Int? = null,

	@SerializedName("summary")
	val summary: String? = null,

	@SerializedName("isSelected")
	val isSelected: Boolean? = false,

	@SerializedName("count")
	var count: Int? = 0,

	@SerializedName("optionImage")
	var optionImage: String? = BLANK_STRING,

	@SerializedName("optionType")
	var optionType: String? = BLANK_STRING,

)
