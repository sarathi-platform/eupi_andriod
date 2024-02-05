package com.nudge.core

import com.nudge.core.eventswriter.DbEventWrite
import com.nudge.core.eventswriter.TextFileEventWriter
import com.nudge.core.eventswriter.IEventWriter
import com.nudge.core.eventswriter.LogEventWriter
import com.nudge.core.eventswriter.entities.ImageEventWriter


const val BLANK_STRING = ""

const val EventsTable = "events_table"
const val EventDependencyTable = "event_dependency_table"

// Sync DB Properties
const val SYNC_MANAGER_DATABASE = "SyncManagerDatabase"
const val SYNC_MANAGER_DB_VERSION = 1


// Increase DB Version everytime any change is made to any table or a new table is added.
const val NUDGE_DATABASE = "NudgeDatabase"
const val NUDGE_DATABASE_VERSION = 2
const val LOCAL_BACKUP_FILE_NAME="Sarathi_event_backup"
const val SARATHI_DIRECTORY_NAME="/SARATHI"
const val LOCAL_BACKUP_EXTENSION=".txt"
const val EVENT_DELIMETER="~@-"
const val ZIP_MIME_TYPE="application/zip"

 val eventWriters = listOf<IEventWriter>(TextFileEventWriter(), DbEventWrite(),LogEventWriter(),ImageEventWriter())