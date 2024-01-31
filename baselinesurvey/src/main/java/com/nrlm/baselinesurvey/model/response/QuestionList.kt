package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem

data class QuestionList (
    @SerializedName("questionId")
    @Expose
    var questionId: Int? = null,

    @SerializedName("questionDisplay")
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
    var options: List<OptionsItem> = listOf(),

    @SerializedName("questionSummary")
    @Expose
    var questionSummary: String? = null,
    //paraphase

    @SerializedName("attributeTag")
    @Expose
    var attributeTag: String? = null,

    @SerializedName("contentList")
    @Expose
    var contentList: List<ContentList> = listOf()

)