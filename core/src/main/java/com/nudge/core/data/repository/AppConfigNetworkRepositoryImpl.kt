package com.nudge.core.data.repository

import com.nudge.core.apiService.CoreApiService
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.AppConfigApiRequest
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class AppConfigNetworkRepositoryImpl @Inject constructor(
    val coreApiService: CoreApiService,
    val coreSharedPrefs: CoreSharedPrefs
) : AppConfigNetworkRepository {
    override suspend fun getAppConfigFromNetwork(): ApiResponseModel<HashMap<String, String>> {

        return coreApiService.fetchAppConfig(
            AppConfigApiRequest(
                mobileNo = coreSharedPrefs.getMobileNo(),
                propertyName = AppConfigKeysEnum.values().map { it.name })
        )
    }

}