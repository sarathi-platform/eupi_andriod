package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase

import com.sarathi.smallgroupmodule.data.model.SubjectAttendanceState
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository.UpdateAttendanceToDbRepository
import javax.inject.Inject

class DeleteAttendanceToDbUseCase @Inject constructor(
    private val updateAttendanceToDbRepository: UpdateAttendanceToDbRepository
) {

    suspend operator fun invoke(
        smallGroupId: Int,
        date: Long,
        finalAttendanceStateList: List<SubjectAttendanceState>,
        onSuccess: () -> Unit
    ) {
        updateAttendanceToDbRepository.softDeleteOldAttendanceForDate(
            finalAttendanceStateList,
            date
        )
        onSuccess()

    }

}