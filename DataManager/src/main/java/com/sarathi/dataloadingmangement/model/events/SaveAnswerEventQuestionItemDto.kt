package com.sarathi.dataloadingmangement.model.events

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveAnswerEventQuestionItemDto(
    @SerializedName("options")
    @Expose
    val options: List<SaveAnswerEventOptionItemDto>,
    @SerializedName("questionType")
    @Expose
    val questionType: String,
    @SerializedName("tag")
    @Expose
    val tag: List<Int>,
    @SerializedName("showQuestion")
    @Expose
    val showQuestion: Boolean = true,
    @SerializedName("questionId")
    @Expose
    val questionId: Int,
    @SerializedName("formId")
    @Expose
    val formId: Int,
    @SerializedName("questionDesc")
    @Expose
    val questionDesc: String,

    @SerializedName("order")
    @Expose
    val order: Int? = 0

)