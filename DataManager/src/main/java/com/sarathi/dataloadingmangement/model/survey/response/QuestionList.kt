package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING


data class QuestionList(
    @SerializedName("questionId")
    @Expose
    var questionId: Int? = null,

    @SerializedName("description")
    @Expose
    var questionDisplay: String? = null,
    // replace with dec

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

    @SerializedName("paraphrase")
    @Expose
    var questionSummary: String? = null,
    //paraphase

    @SerializedName("tag")
    @Expose
    var attributeTag: Int? = null,

    @SerializedName("contents")
    @Expose
    var contentList: List<ContentList> = listOf(),

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

    )