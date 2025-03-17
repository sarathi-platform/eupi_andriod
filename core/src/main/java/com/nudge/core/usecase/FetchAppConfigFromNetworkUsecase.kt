package com.nudge.core.usecase

import com.google.android.gms.common.api.ApiException
import com.nudge.core.SUCCESS
import com.nudge.core.data.repository.AppConfigDatabaseRepository
import com.nudge.core.data.repository.AppConfigNetworkRepository
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.utils.CoreLogger
import javax.inject.Inject

class FetchAppConfigFromNetworkUseCase @Inject constructor(
    val apiConfigNetworkRepository: AppConfigNetworkRepository,
    val apiConfigDatabaseRepository: AppConfigDatabaseRepository
) {

    suspend operator fun invoke(
        propertiesName: List<String> = AppConfigKeysEnum.values().map { it.name },
        onApiSuccess: () -> Unit = {}
    ): Boolean {
        try {
            val startTime = System.currentTimeMillis()

            val apiResponse = apiConfigNetworkRepository.getAppConfigFromNetwork(propertiesName)
            CoreLogger.d(
                tag = "LazyLoadAnalysis",
                msg = "FetchAppConfigFromNetworkUseCase :/registry-service/property  : ${System.currentTimeMillis() - startTime}"
            )

            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    apiConfigDatabaseRepository.saveAppConfig(apiResponse.data)
                    onApiSuccess()
                }
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "FetchAppConfigFromNetworkUseCase : ${System.currentTimeMillis() - startTime}"
                )
                return true
            } else {
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "FetchAppConfigFromNetworkUseCase : ${System.currentTimeMillis() - startTime}"
                )
                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }
}