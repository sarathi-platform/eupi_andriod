package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository

import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceState

interface SaveAttendanceToDbRepository {

    suspend fun saveFinalAttendanceToDb(finalAttendanceStateList: List<SubjectAttendanceState>)

    suspend fun saveAttendanceToSubjectAttributeTable(finalAttendanceState: SubjectAttendanceState): Long

    suspend fun saveAttendanceAttributeToReferenceTable(
        finalAttendanceStateList: List<SubjectAttendanceState>,
        referenceIdMap: Map<Int, Long>
    )

}
