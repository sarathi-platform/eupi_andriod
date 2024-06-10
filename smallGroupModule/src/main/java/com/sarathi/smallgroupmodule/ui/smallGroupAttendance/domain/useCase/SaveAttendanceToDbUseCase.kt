package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase

import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.SaveAttendanceToDbRepository
import javax.inject.Inject

class SaveAttendanceToDbUseCase @Inject constructor(
    private val saveAttendanceToDbRepository: SaveAttendanceToDbRepository
) {

    suspend operator fun invoke(finalAttendanceStateList: List<SubjectAttendanceState>) {
        saveAttendanceToDbRepository.saveFinalAttendanceToDb(finalAttendanceStateList)
    }

}
