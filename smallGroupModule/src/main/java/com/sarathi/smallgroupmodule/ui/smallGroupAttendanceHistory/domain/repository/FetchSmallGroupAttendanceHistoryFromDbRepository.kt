package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupAttendanceHistoryModel
import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceHistoryState

interface FetchSmallGroupAttendanceHistoryFromDbRepository {

    suspend fun fetchSmallGroupAttendanceHistoryFromDb(
        smallGroupId: Int,
        dateRange: Pair<Long, Long>
    ): List<SubjectAttendanceHistoryState>

    suspend fun fetchSmallGroupHistory(
        smallGroupId: Int,
        subjectIds: List<Int>,
        dateRange: Pair<Long, Long>
    ): List<SmallGroupAttendanceHistoryModel>

    suspend fun fetchSubjectDetailsForSmallGroup(subjectIds: List<Int>): List<SubjectEntity>

    suspend fun fetchSubjectIdsForSmallGroup(smallGroupId: Int): List<Int>


}
