package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName

data class EditDidiWealthRankingRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("type") var type: String,
    @SerializedName("result") var result: String
)
