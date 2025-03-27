package com.nudge.core.data.repository.api

import com.nudge.core.database.entities.api.ApiCallConfigEntity

interface IApiCallConfigRepository {
    fun getApiCallList(
        screenName: String,
        dataLoadingTriggerType: String
    ): List<ApiCallConfigEntity>

}