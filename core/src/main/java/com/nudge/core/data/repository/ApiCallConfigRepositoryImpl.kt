package com.nudge.core.data.repository

import com.nudge.core.database.dao.api.ApiCallConfigDao
import com.nudge.core.database.entities.api.ApiCallConfigEntity
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class ApiCallConfigRepositoryImpl @Inject constructor(
    val apiCallConfigDao: ApiCallConfigDao,
    val coreSharedPrefs: CoreSharedPrefs
) : IApiCallConfigRepository {
    override fun getApiCallList(
        screenName: String,
        dataLoadingTriggerType: String
    ): List<ApiCallConfigEntity> {
        return apiCallConfigDao.getApiCallConfigForScreen(
            screenName = screenName,
            triggerPoint = dataLoadingTriggerType,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }
}