package com.nudge.core

import com.nudge.core.eventswriter.DbEventWrite
import com.nudge.core.eventswriter.IEventWriter
import com.nudge.core.eventswriter.LogEventWriter
import com.nudge.core.eventswriter.TextFileEventWriter
import com.nudge.core.eventswriter.entities.ImageEventWriter


const val BLANK_STRING = ""

const val EventsTable = "events_table"
const val EventDependencyTable = "event_dependency_table"
const val ApiStatusTable = "api_status_table"

// Sync DB Properties
const val SYNC_MANAGER_DATABASE = "SyncManagerDatabase"
const val SYNC_MANAGER_DB_VERSION = 1

//FirebaseDb Properties
const val EVENTS_BACKUP_COLLECTION = "EventsBackUp"


// Increase DB Version everytime any change is made to any table or a new table is added.
const val NUDGE_DATABASE = "NudgeDatabase"
const val NUDGE_DATABASE_VERSION = 2

const val SELECTION_MISSION = "Selection"
const val KEY_PARENT_ENTITY_TOLA_ID = "tolaId"
const val KEY_PARENT_ENTITY_DIDI_ID = "didiId"
const val KEY_PARENT_ENTITY_TOLA_NAME = "tolaName"
const val KEY_PARENT_ENTITY_VILLAGE_ID = "villageId"
const val KEY_PARENT_ENTITY_DIDI_NAME = "didiName"
const val KEY_PARENT_ENTITY_DADA_NAME = "dadaName"
const val KEY_PARENT_ENTITY_ADDRESS = "didiAddress"

const val LOCAL_BACKUP_FILE_NAME = "Sarathi_events_"
const val LOCAL_BACKUP__IMAGE_FILE_NAME = "Sarathi_events_Image"
const val SARATHI_DIRECTORY_NAME = "/SARATHI"
const val SARATHI="SARATHI"
const val LOCAL_BACKUP_EXTENSION = ".txt"
const val EVENT_DELIMETER = "~@-"
const val ZIP_MIME_TYPE = "application/zip"
const val EXCEL_TYPE = "text/csv"
const val REGENERATE_PREFIX = "regenerate_"
const val PREF_KEY_IS_SETTING_SCREEN_OPEN= "is_setting_open"
const val ENGLISH_LANGUAGE_CODE="en"
const val UPCM_USER="Ultra Poor change maker (UPCM)"
const val SUFFIX_IMAGE_ZIP_FILE = "Image"
const val SUFFIX_EVENT_ZIP_FILE = "file"
const val BASELINE = "BASELINE"
const val HAMLET = "HAMLET"
const val SELECTION = "SELECTION"

const val ZIP_EXTENSION = "zip"
const val EVENT_STRING="event"
val eventWriters = listOf<IEventWriter>(
    TextFileEventWriter(),
    DbEventWrite(),
    LogEventWriter(),
    ImageEventWriter()
)