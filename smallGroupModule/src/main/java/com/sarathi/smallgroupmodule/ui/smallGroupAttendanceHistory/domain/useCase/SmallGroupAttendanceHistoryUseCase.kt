package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.useCase

import javax.inject.Inject

data class SmallGroupAttendanceHistoryUseCase @Inject constructor(
    val fetchSmallGroupDetailsFromDbUseCase: FetchSmallGroupDetailsFromDbUseCase,
    val fetchSmallGroupAttendanceHistoryFromDbUseCase: FetchSmallGroupAttendanceHistoryFromDbUseCase
)
