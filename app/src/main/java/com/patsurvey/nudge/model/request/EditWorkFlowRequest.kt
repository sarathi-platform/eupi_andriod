package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName

data class EditWorkFlowRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("status") var status: String
)
