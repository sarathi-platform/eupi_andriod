package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity

data class SmallGroupAttendanceEntityState(
    val subjectId: Int,
    val subjectEntity: SubjectEntity,
    var attendance: Boolean
)
