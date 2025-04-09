package com.sarathi.dataloadingmangement.model

import com.nudge.core.enums.ApiStatus

data class ApiStatusStateModel(
    var apiStatus: ApiStatus = ApiStatus.IDEAL,
    var failedApiCount: Float = 0f,
    var totalApiCall: Int = -1,
    var completedApiCount: Float = 0f
)
