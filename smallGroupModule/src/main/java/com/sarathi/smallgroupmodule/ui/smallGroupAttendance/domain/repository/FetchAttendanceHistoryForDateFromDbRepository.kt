package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupAttendanceHistoryModel
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceHistoryState

interface FetchAttendanceHistoryForDateFromDbRepository {

    suspend fun fetchSmallGroupAttendanceHistoryForDate(
        smallGroupId: Int,
        selectedDate: Long
    ): List<SubjectAttendanceHistoryState>

    suspend fun fetchSmallGroupHistory(
        smallGroupId: Int,
        subjectIds: List<Int>,
        dateRange: Long
    ): List<SmallGroupAttendanceHistoryModel>

    suspend fun fetchSubjectDetailsForSmallGroup(subjectIds: List<Int>): List<SubjectEntity>

    suspend fun fetchSubjectIdsForSmallGroup(smallGroupId: Int): List<Int>

}
