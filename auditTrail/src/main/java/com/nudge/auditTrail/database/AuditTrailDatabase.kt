package com.nudge.auditTrail.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nudge.auditTrail.database.dao.AuditTrailDao
import com.nudge.auditTrail.entities.AuditTrailEntity
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.database.converters.ListConvertor

const val AUDIT_TRAIL_DATABASE_VERSION = 1

@Database(
    entities = [
        AuditTrailEntity::class,

    ],
    version = AUDIT_TRAIL_DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConvertor::class)
abstract class CoreDatabase : RoomDatabase() {
    abstract  fun auditTrailDao():AuditTrailDao
}



/////
//@Database(
//    entities = [
//        AuditTrailEntity::class,
//        EventDependencyEntity::class,
//        ApiStatusEntity::class
//    ],
//    version = AUDIT_TRAIL_DATABASE_VERSION,
//    exportSchema = false
//)
//@TypeConverters(DateConverter::class, ListConvertor::class)
//abstract class AuditTrailDatabase : RoomDatabase() {
//
//    abstract fun eventsDao(): EventsDao
//
//    abstract fun eventsDependencyDao(): EventDependencyDao
//
//    abstract fun apiStatusDao(): ApiStatusDao
//
//}