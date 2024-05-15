package com.patsurvey.nudge.activities.ui.login.dataloadingscreen.domain

import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.domain.use_case.FetchMissionDataFromNetworkUseCase

data class FetchDataUseCase(
    val fetchMissionDataFromNetworkUseCase: FetchMissionDataFromNetworkUseCase,
) {
}