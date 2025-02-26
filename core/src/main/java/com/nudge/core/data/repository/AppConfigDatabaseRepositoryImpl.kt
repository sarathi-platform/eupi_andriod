package com.nudge.core.data.repository

import android.util.Base64
import com.nudge.core.BLANK_STRING
import com.nudge.core.REMOTE_CONFIG_SHOW_QUESTION_INDEX_ENABLE
import com.nudge.core.REMOTE_CONFIG_SYNC_ENABLE
import com.nudge.core.REMOTE_CONFIG_SYNC_OPTION_ENABLE
import com.nudge.core.database.dao.ApiConfigDao
import com.nudge.core.database.entities.AppConfigEntity
import com.nudge.core.enums.AppConfigKeysEnum
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
        data.toList().forEach {

            appConfigEntities.add(
                AppConfigEntity.getAppConfigEntity(
                    key = it.first,
                    value = it.second,
                    userId = userId
                )
            )
        }

        appConfigDao.insertAll(appConfigEntities)
        saveIntoSharedPreference(data)
    }

    suspend fun saveIntoSharedPreference(data: HashMap<String, String>) {
        if (data.containsKey(AppConfigKeysEnum.DATA_TAB_VISIBILITY.name)) {
            coreSharedPrefs.saveDataTabVisibility(data[AppConfigKeysEnum.DATA_TAB_VISIBILITY.name].toBoolean())
        }
        if (data.containsKey(AppConfigKeysEnum.SYNC_ENABLED_V2.name)) {
            coreSharedPrefs.savePref(
                REMOTE_CONFIG_SYNC_OPTION_ENABLE,
                data[AppConfigKeysEnum.SYNC_ENABLED_V2.name].toBoolean()
            )
        }
        if (data.containsKey(AppConfigKeysEnum.SYNC_ENABLED.name)) {
            coreSharedPrefs.savePref(
                REMOTE_CONFIG_SYNC_ENABLE,
                data[AppConfigKeysEnum.SYNC_ENABLED.name].toBoolean()
            )
        }
        if (data.containsKey(AppConfigKeysEnum.MIX_PANEL_KEY.name)) {
            coreSharedPrefs.saveMixPanelToken(
                data[AppConfigKeysEnum.MIX_PANEL_KEY.name].toString()
            )
        }
        if (data.containsKey(AppConfigKeysEnum.SENSITIVE_INFO_KEY.name)) {
            coreSharedPrefs.savePref(
                AppConfigKeysEnum.SENSITIVE_INFO_KEY.name,
                Base64.encodeToString(
                    data[AppConfigKeysEnum.SENSITIVE_INFO_KEY.name].toString().toByteArray(),
                    Base64.DEFAULT
                )
            )
        }
        if (data.containsKey(AppConfigKeysEnum.SHOW_QUESTION_INDEX.name)) {
            coreSharedPrefs.savePref(
                REMOTE_CONFIG_SHOW_QUESTION_INDEX_ENABLE,
                data[AppConfigKeysEnum.SHOW_QUESTION_INDEX.name].toBoolean()
            )
        }
        // TODO Uncomment code after navigation is fixed.
        /*if (data.containsKey(AppConfigKeysEnum.SOFT_EVENT_LIMIT_THRESHOLD.name)) {
            data[AppConfigKeysEnum.SOFT_EVENT_LIMIT_THRESHOLD.name]?.toInt()?.let {
                coreSharedPrefs.setSoftEventLimitThreshold(it)
            }
        }

        if (data.containsKey(AppConfigKeysEnum.HARD_EVENT_LIMIT_THRESHOLD.name)) {
            data[AppConfigKeysEnum.HARD_EVENT_LIMIT_THRESHOLD.name]?.toInt()?.let {
                coreSharedPrefs.setHardEventLimitThreshold(it)
            }
        }*/

        if (data.containsKey(AppConfigKeysEnum.USE_BASELINE_V1.name)) {
            data[AppConfigKeysEnum.USE_BASELINE_V1.name]?.toString()?.let {
                coreSharedPrefs.savePref(AppConfigKeysEnum.USE_BASELINE_V1.name, it)
            }
        }
        if (data.containsKey(AppConfigKeysEnum.SYNC_CONSUMER_BAR_VISIBILITY.name)) {
            coreSharedPrefs.savePref(
                AppConfigKeysEnum.SYNC_CONSUMER_BAR_VISIBILITY.name,
                data[AppConfigKeysEnum.SYNC_CONSUMER_BAR_VISIBILITY.name].toBoolean()
            )
        }

    }

    override suspend fun getAppConfig(key: String): String {
        return appConfigDao.getConfig(
            key = key,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )?.value ?: BLANK_STRING
    }

    override fun getAppConfigFromPref(key: String): String {
        return coreSharedPrefs.getPref(key, BLANK_STRING)
    }
}