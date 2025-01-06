package com.nudge.core.data.repository

import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.preference.CorePrefRepo
import javax.inject.Inject

class SyncMigrationRepositoryImpl @Inject constructor(
    val eventsDao: EventsDao,
    val eventDependencyDao: EventDependencyDao,
    val corePrefRepo: CorePrefRepo
) : SyncMigrationRepository {

    override suspend fun deleteEventsAfter1To2Migration() {
        eventsDao.deleteAllEvents()
        eventDependencyDao.deleteAllDependentEvents()
    }

    override fun isSyncDBMigrate(): Boolean {
        return corePrefRepo.isSyncDBMigrate()
    }

    override fun setSyncDBMigrateToFalse() {
        corePrefRepo.setSyncDBMigrate(false)
    }
}