package com.nudge.core.usecase

import android.text.TextUtils
import com.nudge.core.DEFAULT_BASELINE_V1_IDS
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

    fun getBaselineV1Ids(): String {
        var baseline_v1_ids =
            invokeFromPref(AppConfigKeysEnum.USE_BASELINE_V1.name)
        if (TextUtils.isEmpty(baseline_v1_ids)) {
            baseline_v1_ids = DEFAULT_BASELINE_V1_IDS
        }
        return baseline_v1_ids
    }
}