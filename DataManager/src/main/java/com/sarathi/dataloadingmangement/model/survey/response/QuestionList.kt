package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING


data class QuestionList(
    @SerializedName("questionId")
    @Expose
    var questionId: Int? = null,


    @SerializedName("order")
    @Expose
    var order: Int? = null,

    @SerializedName("type")
    @Expose
    var type: String? = null,

    @SerializedName("gotoQuestionId")
    @Expose
    var gotoQuestionId: Int? = null,

    @SerializedName("options")
    @Expose
    var options: List<OptionsItem?>? = emptyList(),


    @SerializedName("tag")
    @Expose
    var attributeTag: List<Int>? = null,
    @SerializedName("formId")
    @Expose
    var formId: Int? = null,

    @SerializedName("contents")
    @Expose
    var contentList: List<ContentList>? = listOf(),

    @SerializedName("imageIcon")
    @Expose
    val imageIcon: String = BLANK_STRING,
    @SerializedName("languageCode")
    @Expose
    val languageCode: String = BLANK_STRING,
    @SerializedName("conditional")
    @Expose
    val conditional: Boolean = false,
    @SerializedName("isMandatory")
    @Expose
    val isMandatory: Boolean = false,
    @SerializedName("languages")
    @Expose
    val surveyLanguageAttributes: List<SurveyLanguageAttributes>?,
    @SerializedName("originalValue")
    @Expose
    val originalValue: String = BLANK_STRING,

    @SerializedName("conditions")
    @Expose
    val conditions: List<Conditions>? = listOf(),

    @SerializedName("conditionsOpererator")
    @Expose
    val conditionsOpererator: String? = null,
    @SerializedName("formOrder")
    @Expose
    var formOrder: Int? = null,


    )

data class Conditions(
    @SerializedName("expression")
    @Expose
    var expression: String? = null,

    @SerializedName("sourceQuestion")
    @Expose
    var sourceQuestion: Int? = null,
)