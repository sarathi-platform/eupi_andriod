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
import com.nudge.core.database.entities.ApiStatusEntity
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.ImageStatusEntity
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
import java.sql.SQLException

@Database(
    entities = [
        Events::class,
        EventDependencyEntity::class,
        ApiStatusEntity::class,
        EventStatusEntity::class,
        ImageStatusEntity::class
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

    companion object {
        private const val ADD_REQUEST_ID_IN_EVENT_STATUS_TABLE =
            "ALTER TABLE 'events_status_table' ADD COLUMN 'request_id' STRING"
        private const val ADD_REQUEST_ID_IMAGE_STATUS_TABLE =
            "ALTER TABLE 'image_status_table' ADD COLUMN 'request_id' STRING"

        // CREATE MIGRATION OBJECT FOR MIGRATION 1 to 2.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                migration(
                    db,
                    listOf(ADD_REQUEST_ID_IN_EVENT_STATUS_TABLE, ADD_REQUEST_ID_IMAGE_STATUS_TABLE)
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