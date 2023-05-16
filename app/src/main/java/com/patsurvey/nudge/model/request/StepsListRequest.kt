package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.database.StepListEntity

data class StepsListRequest(
    @SerializedName("programName") var programName: String,
    @SerializedName("stepList") var stepList: List<StepListEntity>
)
