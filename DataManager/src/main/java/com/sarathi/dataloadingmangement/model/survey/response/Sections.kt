package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.nudge.core.NO_SECTION


data class Sections(
    @SerializedName("sectionId")
    @Expose
    val sectionId: Int = 0,

    @SerializedName("sectionName")
    @Expose
    val sectionName: String = NO_SECTION,

    @SerializedName("order")
    @Expose
    val sectionOrder: Int = 1,

    @SerializedName("description")
    @Expose
    val sectionDetails: String = BLANK_STRING,

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

    @SerializedName("languageCode")
    @Expose
    val languageCode: String = BLANK_STRING,
    @SerializedName("paraphrase")
    @Expose
    val paraphrase: String = BLANK_STRING,
)