package com.nudge.syncmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nudge.core.database.entities.Events
import com.nudge.syncmanager.database.converters.DateConverter
import com.nudge.syncmanager.database.dao.EventsDao

@Database(
    entities = [
        Events::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class SyncManagerDatabase: RoomDatabase() {

    abstract fun eventsDao(): EventsDao

}