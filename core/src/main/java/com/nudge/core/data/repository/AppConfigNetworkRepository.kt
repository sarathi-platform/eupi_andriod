package com.nudge.core.data.repository

import com.nudge.core.model.ApiResponseModel

interface AppConfigNetworkRepository {

    suspend fun getAppConfigFromNetwork(): ApiResponseModel<HashMap<String, String>>
}