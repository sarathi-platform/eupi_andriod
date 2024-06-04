package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase

import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.useCase.FetchSmallGroupDetailsFromDbUseCase
import javax.inject.Inject

data class SmallGroupAttendanceUserCase @Inject constructor(
    val fetchSmallGroupDetailsFromDbUseCase: FetchSmallGroupDetailsFromDbUseCase,
    val fetchDidiListForSmallGroupFromDbUseCase: FetchDidiListForSmallGroupFromDbUseCase
)