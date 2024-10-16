package com.nudge.syncmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nudge.core.SYNC_MANAGER_DB_VERSION
import com.nudge.core.database.converters.DateConverter
import com.nudge.core.database.converters.ListConvertor
import com.nudge.core.database.dao.ApiStatusDao
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.dao.RequestStatusDao
import com.nudge.core.database.entities.ApiStatusEntity
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.ImageStatusEntity
import com.nudge.core.database.entities.RequestStatusEntity
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.database.SyncMigrationQueries.ADD_EVENT_ID_IN_EVENT_TABLE
import com.nudge.syncmanager.database.SyncMigrationQueries.ADD_REQUEST_ID_IN_EVENT_TABLE
import com.nudge.syncmanager.database.SyncMigrationQueries.ALTER_EVENT_TABLE_COLUMN_CONSUMER_STATUS_DROP
import com.nudge.syncmanager.database.SyncMigrationQueries.ALTER_EVENT_TABLE_COLUMN_RESULT_DROP
import com.nudge.syncmanager.database.SyncMigrationQueries.CREATE_EVENT_STATUS_TABLE
import com.nudge.syncmanager.database.SyncMigrationQueries.CREATE_IMAGE_STATUS_TABLE
import com.nudge.syncmanager.database.SyncMigrationQueries.CREATE_REQUEST_STATUS_TABLE
import java.sql.SQLException

@Database(
    entities = [
        Events::class,
        EventDependencyEntity::class,
        ApiStatusEntity::class,
        EventStatusEntity::class,
        ImageStatusEntity::class,
        RequestStatusEntity::class
    ],
    version = SYNC_MANAGER_DB_VERSION,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConvertor::class)
abstract class SyncManagerDatabase : RoomDatabase() {

    abstract fun eventsDao(): EventsDao

    abstract fun eventsDependencyDao(): EventDependencyDao

    abstract fun apiStatusDao(): ApiStatusDao
    abstract fun eventStatusDao(): EventStatusDao

    abstract fun imageStatusDao(): ImageStatusDao
    abstract fun requestStatusDao(): RequestStatusDao

    companion object {

        // CREATE MIGRATION OBJECT FOR MIGRATION 1 to 2.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                migration(
                    db,
                    listOf(
                        ALTER_EVENT_TABLE_COLUMN_RESULT_DROP,
                        ALTER_EVENT_TABLE_COLUMN_CONSUMER_STATUS_DROP,
                        ADD_REQUEST_ID_IN_EVENT_TABLE,
                        ADD_EVENT_ID_IN_EVENT_TABLE,
                        CREATE_EVENT_STATUS_TABLE,
                        CREATE_IMAGE_STATUS_TABLE,
                        CREATE_REQUEST_STATUS_TABLE
                    )
                )
            }
        }

        private fun migration(database: SupportSQLiteDatabase, execSqls: List<String>) {
            for (sql in execSqls) {
                try {
                    database.execSQL(sql)
                } catch (e: SQLException) {
                    CoreLogger.e(
                        CoreAppDetails.getApplicationContext().applicationContext,
                        "SyncManagerDatabase",
                        "migration \"$sql\" Migration Error",
                        e
                    )
                } catch (t: Throwable) {
                    CoreLogger.e(
                        CoreAppDetails.getApplicationContext().applicationContext,
                        "SyncManagerDatabase",
                        "migration \"$sql\"",
                        t
                    )
                }
            }
        }

    }
}