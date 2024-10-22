package com.nudge.core.usecase

import com.google.android.gms.common.api.ApiException
import com.nudge.core.SUCCESS
import com.nudge.core.data.repository.AppConfigDatabaseRepository
import com.nudge.core.data.repository.AppConfigNetworkRepository
import com.nudge.core.enums.AppConfigKeysEnum
import javax.inject.Inject

class FetchAppConfigFromNetworkUseCase @Inject constructor(
    val apiConfigNetworkRepository: AppConfigNetworkRepository,
    val apiConfigDatabaseRepository: AppConfigDatabaseRepository
) {

    suspend operator fun invoke(
        propertiesName: List<String> = AppConfigKeysEnum.values().map { it.name }
    ): Boolean {
        try {
            val apiResponse = apiConfigNetworkRepository.getAppConfigFromNetwork(propertiesName)
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

    suspend fun deleteEventsDataAfterMigration() {
        apiConfigDatabaseRepository.deleteEventsDataAfterMigration()
    }
}