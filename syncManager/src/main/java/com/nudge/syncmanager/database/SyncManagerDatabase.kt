package com.nudge.syncmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nudge.core.SYNC_MANAGER_DB_VERSION
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.database.converters.ListConvertor
import com.nudge.core.database.dao.ApiStatusDao
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.ApiStatusEntity
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.database.entities.Events

@Database(
    entities = [
        Events::class,
        EventDependencyEntity::class,
        ApiStatusEntity::class,
        EventStatusEntity::class
    ],
    version = SYNC_MANAGER_DB_VERSION,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConvertor::class)
abstract class SyncManagerDatabase : RoomDatabase() {

    abstract fun eventsDao(): EventsDao

    abstract fun eventsDependencyDao(): EventDependencyDao

    abstract fun apiStatusDao(): ApiStatusDao
    abstract fun eventStatusDao():EventStatusDao

}