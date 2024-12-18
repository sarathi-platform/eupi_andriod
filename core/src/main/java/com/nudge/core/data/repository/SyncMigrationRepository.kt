package com.nudge.core.data.repository

interface SyncMigrationRepository {
    suspend fun deleteEventsAfter1To2Migration()
    fun isSyncDBMigrate(): Boolean
    fun setSyncDBMigrateToFalse()
}