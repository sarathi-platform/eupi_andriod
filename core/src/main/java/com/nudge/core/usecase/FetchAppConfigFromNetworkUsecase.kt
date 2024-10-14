package com.nudge.core.usecase

import com.google.android.gms.common.api.ApiException
import com.nudge.core.SUCCESS
import com.nudge.core.data.repository.AppConfigDatabaseRepository
import com.nudge.core.data.repository.AppConfigNetworkRepository
import javax.inject.Inject

class FetchAppConfigFromNetworkUseCase @Inject constructor(
    val apiConfigNetworkRepository: AppConfigNetworkRepository,
    val apiConfigDatabaseRepository: AppConfigDatabaseRepository
) {

    suspend operator fun invoke(): Boolean {
        try {
            val apiResponse = apiConfigNetworkRepository.getAppConfigFromNetwork()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    apiConfigDatabaseRepository.saveAppConfig(apiResponse.data)
                }
                return true
            } else {
                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }
}