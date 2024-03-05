package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.model.request.Options

data class Answers(
    @SerializedName("options")
    @Expose
    var options: List<Options> = listOf(),

    @SerializedName("questionId")
    @Expose
    var questionId: Int,

    @SerializedName("displayQuestion")
    @Expose
    var displayQuestion: String,

    @SerializedName("score")
    @Expose
    var score: Int,

    @SerializedName("totalWeight")
    @Expose
    var totalWeight: Int,

    @SerializedName("ratio")
    @Expose
    var ratio: Int,

    @SerializedName("section")
    @Expose
    var section: String,

    @SerializedName("summary")
    @Expose
    var summary: String,

    @SerializedName("questionType")
    @Expose
    var questionType: String,

    @SerializedName("assetAmount")
    @Expose
    var assetAmount: Int
)
