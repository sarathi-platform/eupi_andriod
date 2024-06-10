package com.sarathi.smallgroupmodule.data.model

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity

data class SubjectAttendanceHistoryState(
    val subjectId: Int,
    val subjectEntity: SubjectEntity,
    val attendance: Boolean,
    val date: Long
)
