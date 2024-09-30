package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING

data class OptionsItem(
    @SerializedName("weight")
    val weight: Int? = null,

    @SerializedName("optionId")
    val optionId: Int? = null,

    @SerializedName("optionValue")
    val optionValue: Int? = null,

    @SerializedName("count")
    var count: Int? = 0,

    @SerializedName("optionImage")
    var optionImage: String? = BLANK_STRING,

    @SerializedName("type")
    var optionType: String? = BLANK_STRING,

    @SerializedName("conditional")
    @Expose
    val conditional: Boolean = false,

    @SerializedName("languageCode")
    @Expose
    val languageCode: String = BLANK_STRING,

    @SerializedName("order")
    @Expose
    val order: Int = 0,

    @SerializedName("values")
    @Expose
    val values: List<ValuesDto> = listOf(),

    @SerializedName("conditions")
    @Expose
    val conditions: List<ConditionsDto?>? = emptyList(),

    @SerializedName("contents")
    @Expose
    val contentList: List<ContentList>? = listOf(),
    @SerializedName("languages")
    @Expose
    val surveyLanguageAttributes: List<SurveyLanguageAttributes>?,
    @SerializedName("originalValue")
    @Expose
    val originalValue: String = BLANK_STRING,
)
