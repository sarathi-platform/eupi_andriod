package com.patsurvey.nudge.model.response

import com.google.gson.annotations.SerializedName

data class WorkFlowResponse(
    @SerializedName("id") var id: Int,
    @SerializedName("status") var status: String,
    @SerializedName("transactionId") var transactionId: String,
    @SerializedName("villageId") var villageId: Int,
    @SerializedName("programId") var programId: Int,
    @SerializedName("programsProcessId") var programsProcessId : Int
)
