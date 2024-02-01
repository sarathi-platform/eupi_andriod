package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem

data class QuestionList (
    @SerializedName("questionId")
    @Expose
    var questionId: Int? = null,

    @SerializedName("description")
    @Expose
    var questionDisplay: String? = null,

    @SerializedName("order")
    @Expose
    var order: Int? = null,

    @SerializedName("type")
    @Expose
    var type: String? = null,

    @SerializedName("questionId")
    @Expose
    var gotoQuestionId: Int? = null,

    @SerializedName("options")
    @Expose
    var options: List<OptionsItem> = listOf(),

    @SerializedName("paraphrase")
    @Expose
    var questionSummary: String? = null,

    @SerializedName("tag")
    @Expose
    var attributeTag: String? = null,

    @SerializedName("contents")
    @Expose
    var contentList: List<ContentList> = listOf(),

    val imageIcon: String,
    val languageCode: String,
    //val values: Any
)