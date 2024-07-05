package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING


data class Sections(
    @SerializedName("sectionId")
    @Expose
    val sectionId: Int = 0,

    @SerializedName("order")
    @Expose
    val sectionOrder: Int = 1,


    @SerializedName("imageIcon")
    @Expose
    val sectionIcon: String = BLANK_STRING,
//    val sectionIcon: Int = 0,

    @SerializedName("contents")
    @Expose
    val contentList: List<ContentList> = listOf(),

    @SerializedName("questions")
    @Expose
    val questionList: List<QuestionList?> = listOf(),
    @SerializedName("languages")
    @Expose
    val surveyLanguageAttributes: List<SurveyLanguageAttributes>,
    @SerializedName("originalValue")
    @Expose
    val originalValue: String = BLANK_STRING,
    @SerializedName("tag")
    @Expose
    val tag: List<Int>? = listOf(),

)