package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase

import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.FetchMarkedDatesRepository
import javax.inject.Inject

class FetchMarkedDatesUseCase @Inject constructor(
    private val fetchMarkedDatesRepository: FetchMarkedDatesRepository
) {

    suspend operator fun invoke(subjectIds: List<Int>): List<Long> {
        return fetchMarkedDatesRepository.fetchMarkedDates(subjectIds)
    }

}
