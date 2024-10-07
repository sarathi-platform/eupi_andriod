package com.nudge.core.data.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.database.dao.ApiConfigDao
import com.nudge.core.database.entities.AppConfigEntity
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class AppConfigDatabaseRepositoryImpl @Inject constructor(
    val appConfigDao: ApiConfigDao,
    val coreSharedPrefs: CoreSharedPrefs
) : AppConfigDatabaseRepository {
    override suspend fun saveAppConfig(data: HashMap<String, String>) {
        val userId = coreSharedPrefs.getUniqueUserIdentifier()
        appConfigDao.deleteAppConfig(userId = userId)
        val appConfigEntities = ArrayList<AppConfigEntity>()
        data.map {
            appConfigEntities.add(
                AppConfigEntity.getAppConfigEntity(
                    key = it.key,
                    value = it.value,
                    userId = userId
                )
            )
        }

        appConfigDao.insertAll(appConfigEntities)

    }

    override suspend fun getAppConfig(key: String): String {
        return appConfigDao.getConfig(
            key = key,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )?.value ?: BLANK_STRING
    }


}