package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase

import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.UpdateAttendanceToDbRepository
import javax.inject.Inject

class UpdateAttendanceToDbUseCase @Inject constructor(
    private val updateAttendanceToDbRepository: UpdateAttendanceToDbRepository
) {

    suspend operator fun invoke(
        updatedAttendanceList: List<SubjectAttendanceState>,
        selectedDate: Long
    ) {
        updateAttendanceToDbRepository.updateFinalAttendanceToDb(
            updatedAttendanceList,
            selectedDate
        )
    }

}
