package com.nudge.core.data.repository


interface AppConfigDatabaseRepository {

    suspend fun saveAppConfig(data: HashMap<String, String>)
    suspend fun getAppConfig(key: String): String
    fun getAppConfigFromPref(key: String): String
    suspend fun deleteEventsDataAfterMigration()
}