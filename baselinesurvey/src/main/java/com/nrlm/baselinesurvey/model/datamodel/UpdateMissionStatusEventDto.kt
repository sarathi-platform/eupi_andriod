package com.nrlm.baselinesurvey.model.datamodel

import com.nrlm.baselinesurvey.utils.states.SectionStatus

data class UpdateMissionStatusEventDto(
    val missionId: Int,
    val actualStartDate: String,
    val completedDate: String,
    val referenceType: String,
    val status: SectionStatus
)