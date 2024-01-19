package com.nrlm.baselinesurvey.model.response

import com.nrlm.baselinesurvey.model.datamodel.MissionActivityModel

data class MissionResponseModel(
    val activities: List<MissionActivityModel>,
    val endDate: String,
    val missionId: Int,
    val missionName: String,
    val startDate: String
)