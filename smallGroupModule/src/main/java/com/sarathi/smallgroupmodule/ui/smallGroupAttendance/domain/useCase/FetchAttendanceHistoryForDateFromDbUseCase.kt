package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase

import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceHistoryState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.FetchAttendanceHistoryForDateFromDbRepository
import javax.inject.Inject

class FetchAttendanceHistoryForDateFromDbUseCase @Inject constructor(
    private val fetchAttendanceHistoryForDateFromDbRepository: FetchAttendanceHistoryForDateFromDbRepository
) {

    suspend operator fun invoke(
        smallGroupId: Int,
        selectedDate: Long
    ): List<SubjectAttendanceHistoryState> {
        return fetchAttendanceHistoryForDateFromDbRepository.fetchSmallGroupAttendanceHistoryForDate(
            smallGroupId,
            selectedDate
        )
    }

}
