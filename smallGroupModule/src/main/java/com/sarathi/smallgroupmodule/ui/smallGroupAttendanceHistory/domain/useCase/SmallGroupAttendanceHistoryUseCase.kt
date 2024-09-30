package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.useCase

import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase.DeleteAttendanceToDbUseCase
import javax.inject.Inject

data class SmallGroupAttendanceHistoryUseCase @Inject constructor(
    val fetchSmallGroupDetailsFromDbUseCase: FetchSmallGroupDetailsFromDbUseCase,
    val fetchSmallGroupAttendanceHistoryFromDbUseCase: FetchSmallGroupAttendanceHistoryFromDbUseCase,
    val deleteAttendanceToDbUseCase: DeleteAttendanceToDbUseCase
)
