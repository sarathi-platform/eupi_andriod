package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.model.dataModel.StepsListModal

data class StepsListRequest(
    @SerializedName("programName") var programName: String,
    @SerializedName("stepList") var stepList: List<StepsListModal>
)
