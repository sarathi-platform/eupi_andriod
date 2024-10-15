package com.nudge.core.usecase

import com.nudge.core.data.repository.AppConfigDatabaseRepository
import javax.inject.Inject

class FetchAppConfigFromCacheOrDbUsecase @Inject constructor(
    val apiConfigDatabaseRepository: AppConfigDatabaseRepository
) {

    suspend operator fun invoke(key: String): String {
        return apiConfigDatabaseRepository.getAppConfig(key)
    }
}