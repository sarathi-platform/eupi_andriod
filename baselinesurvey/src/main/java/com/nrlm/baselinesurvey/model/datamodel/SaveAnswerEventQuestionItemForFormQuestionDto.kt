package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveAnswerEventQuestionItemForFormQuestionDto(
    @SerializedName("options")
    @Expose
    val options: List<List<SaveAnswerEventOptionItemDto>>,
    @SerializedName("questionType")
    @Expose
    val questionType: String,
    @SerializedName("tag")
    @Expose
    val tag: String,
    @SerializedName("showQuestion")
    @Expose
    val showQuestion: Boolean = true,
    @SerializedName("questionId")
    @Expose
    val questionId: Int
)
