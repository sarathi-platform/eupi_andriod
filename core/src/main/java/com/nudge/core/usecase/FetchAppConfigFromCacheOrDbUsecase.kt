package com.nudge.core.usecase

import android.util.Base64
import com.nudge.core.data.repository.AppConfigDatabaseRepository
import com.nudge.core.enums.AppConfigKeysEnum
import javax.inject.Inject

class FetchAppConfigFromCacheOrDbUsecase @Inject constructor(
    val apiConfigDatabaseRepository: AppConfigDatabaseRepository
) {

    suspend operator fun invoke(key: String): String {
        return apiConfigDatabaseRepository.getAppConfig(key)
    }

    fun invokeFromPref(key: String): String {
        return apiConfigDatabaseRepository.getAppConfigFromPref(key)
    }

    fun getAESSecretKey(): String {
        return String(
            Base64.decode(
                invokeFromPref(AppConfigKeysEnum.SENSITIVE_INFO_KEY.name),
                Base64.DEFAULT
            )
        )
    }
}