package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.useCase

import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceHistoryState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository.FetchSmallGroupAttendanceHistoryFromDbRepository
import javax.inject.Inject

class FetchSmallGroupAttendanceHistoryFromDbUseCase @Inject constructor(
    private val fetchSmallGroupAttendanceHistoryFromDbRepository: FetchSmallGroupAttendanceHistoryFromDbRepository
) {

    suspend operator fun invoke(
        smallGroupId: Int,
        dateRange: Pair<Long, Long>
    ): List<SubjectAttendanceHistoryState> {
        return fetchSmallGroupAttendanceHistoryFromDbRepository.fetchSmallGroupAttendanceHistoryFromDb(
            smallGroupId,
            dateRange
        )
    }

    suspend fun fetchSubjectIdsForSmallGroup(smallGroupId: Int): List<Int> {
        return fetchSmallGroupAttendanceHistoryFromDbRepository.fetchSubjectIdsForSmallGroup(
            smallGroupId
        )
    }

}
