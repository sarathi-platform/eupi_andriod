package com.nrlm.baselinesurvey.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FetchSavedSurveyAnswersRequest(
    @SerializedName("beneficiaryId")
    @Expose
    val beneficiaryId: List<Int>
)
