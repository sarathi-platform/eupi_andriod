package com.nudge.syncmanager.database

import com.nudge.core.EVENT_STATUS_TABLE_NAME
import com.nudge.core.IMAGE_STATUS_TABLE_NAME
import com.nudge.core.REQUEST_STATUS_TABLE_NAME

object SyncMigrationQueries {
    val ALTER_EVENT_TABLE_COLUMN_RESULT_DROP =
        "ALTER TABLE 'events_table' DROP COLUMN result"

    val ALTER_EVENT_TABLE_COLUMN_CONSUMER_STATUS_DROP =
        "ALTER TABLE 'events_table' DROP COLUMN consumer_status"

    const val ADD_REQUEST_ID_IN_EVENT_TABLE =
        "ALTER TABLE 'events_table' ADD COLUMN 'requestId' TEXT"
    const val ADD_EVENT_ID_IN_EVENT_TABLE =
        "ALTER TABLE 'events_table' ADD COLUMN 'eventId' TEXT"

    const val CREATE_EVENT_STATUS_TABLE =
        "CREATE TABLE IF NOT EXISTS $EVENT_STATUS_TABLE_NAME (\n" +
                "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    clientId TEXT NOT NULL,\n" +
                "    createdDate INTEGER NOT NULL,\n" +
                "    createdBy TEXT NOT NULL,\n" +
                "    mobileNumber TEXT NOT NULL,\n" +
                "    status TEXT NOT NULL,\n" +
                "    requestId TEXT,\n" +
                "    errorMessage TEXT\n" +
                ")"

    const val CREATE_IMAGE_STATUS_TABLE =
        "CREATE TABLE IF NOT EXISTS $IMAGE_STATUS_TABLE_NAME (\n" +
                "    'id' TEXT PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    name TEXT NOT NULL,\n" +
                "    type TEXT NOT NULL,\n" +
                "    createdDate INTEGER NOT NULL,\n" +
                "    modifiedDate INTEGER NOT NULL,\n" +
                "    createdBy TEXT NOT NULL,\n" +
                "    mobileNumber TEXT NOT NULL,\n" +
                "    fileName TEXT,\n" +
                "    filePath TEXT,\n" +
                "    status TEXT NOT NULL,\n" +
                "    retryCount INTEGER,\n" +
                "    requestId TEXT,\n" +
                "    errorMessage TEXT,\n" +
                "    imageEventId TEXT NOT NULL,\n" +
                "    blobUrl TEXT \n" +
                ")"

    const val CREATE_REQUEST_STATUS_TABLE =
        "CREATE TABLE IF NOT EXISTS $REQUEST_STATUS_TABLE_NAME (\n" +
                "    'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    status TEXT NOT NULL,\n" +
                "    requestId TEXT,\n" +
                "    createdDate INTEGER NOT NULL,\n" +
                "    modifiedDate INTEGER NOT NULL,\n" +
                "    createdBy TEXT NOT NULL,\n" +
                "    mobileNumber TEXT NOT NULL,\n" +
                "    eventCount INTEGER NOT NULL DEFAULT 0\n" +
                ")"
}