package com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case


data class MissionScreenUseCase(
    val fetchMissionDataFromNetworkUseCase: FetchMissionDataFromNetworkUseCase,
    val getMissionListFromDbUseCase: GetMissionListFromDbUseCase,
)
