package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName

data class AddWorkFlowRequest(
    @SerializedName("status") var status: String,
    @SerializedName("villageId") var villageId: Int,
    @SerializedName("programId") var programId: Int,
    @SerializedName("programsProcessId") var programsProcessId : Int
)
