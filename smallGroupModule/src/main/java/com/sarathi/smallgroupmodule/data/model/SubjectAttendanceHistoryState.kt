package com.sarathi.smallgroupmodule.data.model

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity

data class SubjectAttendanceHistoryState(
    val subjectId: Int,
    val subjectEntity: SubjectEntity,
    val attendance: Boolean,
    val date: Long
)


fun SubjectAttendanceHistoryState.convertToSubjectAttendanceState(): SubjectAttendanceState {
    return SubjectAttendanceState(
        subjectId = this.subjectId,
        attendance = this.attendance,
        date = this.date
    )
}

fun List<SubjectAttendanceHistoryState>?.convertToSubjectAttendanceStateList(): List<SubjectAttendanceState> {
    this?.let {
        val subjectAttendanceStateList = ArrayList<SubjectAttendanceState>()
        this.forEach { historyState ->
            subjectAttendanceStateList.add(historyState.convertToSubjectAttendanceState())
        }
        return subjectAttendanceStateList
    } ?: return emptyList()

}