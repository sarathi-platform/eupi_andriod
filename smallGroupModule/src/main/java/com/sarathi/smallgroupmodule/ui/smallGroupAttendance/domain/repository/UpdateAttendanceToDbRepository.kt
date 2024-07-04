package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository

import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceState

interface UpdateAttendanceToDbRepository {

    suspend fun updateFinalAttendanceToDb(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        selectedDate: Long
    )

    suspend fun updateAttendanceToSubjectAttributeTable(finalAttendanceState: SubjectAttendanceState): Long

    suspend fun updateAttendanceAttributeToReferenceTable(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        referenceIdMap: Map<Int, Long>
    )

    suspend fun removeOldAttendanceForDate(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        selectedDate: Long
    )

    suspend fun softDeleteOldAttendanceForDate(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        selectedDate: Long
    )

    suspend fun removeAttendanceFromSubjectAttributeTable(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        selectedDate: Long
    )

    suspend fun removeAttendanceAttributeFromReferenceTable(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        referenceIdMap: Map<Int, Int>
    )

    suspend fun getOldRefForAttendanceAttribute(state: SubjectAttendanceState): Int

    suspend fun softDeleteAttendanceFromSubjectAttributeTable(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        selectedDate: Long
    )

}
