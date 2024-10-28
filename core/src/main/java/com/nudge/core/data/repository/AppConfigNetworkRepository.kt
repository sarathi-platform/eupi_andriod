package com.nudge.core.data.repository

import com.nudge.core.model.ApiResponseModel

interface AppConfigNetworkRepository {

    suspend fun getAppConfigFromNetwork(propertiesName: List<String>): ApiResponseModel<HashMap<String, String>>
}