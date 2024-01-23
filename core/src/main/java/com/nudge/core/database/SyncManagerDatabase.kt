package com.nudge.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nudge.core.EventDependencyTable
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.database.converters.ListConvertor
import com.nudge.core.database.converters.StringJsonConverter
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events

@Database(
    entities = [
        Events::class,
        EventDependencyEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConvertor::class)
abstract class SyncManagerDatabase: RoomDatabase() {

    abstract fun eventsDao(): EventsDao

    abstract fun eventsDependencyDao(): EventDependencyDao

}