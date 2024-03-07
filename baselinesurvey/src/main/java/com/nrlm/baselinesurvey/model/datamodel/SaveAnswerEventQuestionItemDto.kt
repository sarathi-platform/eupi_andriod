package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveAnswerEventQuestionItemDto(
    @SerializedName("options")
    @Expose
    val options: List<SaveAnswerEventOptionItemDto>,
    @SerializedName("questionType")
    @Expose
    val questionType: String,
    @SerializedName("questionId")
    @Expose
    val questionId: Int
)