package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveAnswerEventSectionItemDto(
    @SerializedName("questions")
    @Expose
    val questions: List<SaveAnswerEventQuestionItemDto>,
    @SerializedName("sectionId")
    @Expose
    val sectionId: Int
)