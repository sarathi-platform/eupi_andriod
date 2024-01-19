package com.nudge.syncmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nudge.syncmanager.database.converters.DateConverter
import com.nudge.syncmanager.database.dao.EventsDao
import com.nudge.syncmanager.database.entities.Events

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