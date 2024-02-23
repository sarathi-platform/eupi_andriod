package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.BLANK_STRING

data class EditWorkFlowRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("status") var status: String,
    @SerializedName("localModifiedDate") var localModifiedDate: String? = BLANK_STRING,
    @SerializedName("villageId") var villageId: Int,
    @SerializedName("programsProcessId") var programsProcessId: Int
)
