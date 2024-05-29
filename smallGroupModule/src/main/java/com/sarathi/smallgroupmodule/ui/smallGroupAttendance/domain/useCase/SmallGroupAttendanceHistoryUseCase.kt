package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.useCase

import javax.inject.Inject

data class SmallGroupAttendanceHistoryUseCase @Inject constructor(
    val fetchSmallGroupDetailsFromDbUseCase: FetchSmallGroupDetailsFromDbUseCase
)
