package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.activities.video.VideoListViewModel
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TrainingVideoEntity

data class StepsListRequest(
    @SerializedName("programName") var programName: String,
    @SerializedName("stepList") var stepList: List<StepListEntity>,
    @SerializedName("videosList") var videosList: List<TrainingVideoEntity>
)
