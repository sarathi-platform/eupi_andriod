package com.nudge.core

import com.nudge.core.eventswriter.DbEventWrite
import com.nudge.core.eventswriter.IEventWriter
import com.nudge.core.eventswriter.LogEventWriter
import com.nudge.core.eventswriter.TextFileEventWriter
import com.nudge.core.eventswriter.entities.ImageEventWriter


const val BLANK_STRING = ""
const val DEFAULT_ID = 0
const val NO_SECTION = "NO_SECTION"
const val NO_TOLA_TITLE = "NO TOLA"


const val EventsTable = "events_table"
const val EventsStatusTable = "events_status_table"
const val EventDependencyTable = "event_dependency_table"
const val ApiStatusTable = "api_status_table"
const val ImageStatusTable = "image_status_table"

// Sync DB Properties
const val SYNC_MANAGER_DATABASE = "SyncManagerDatabase"
const val SYNC_MANAGER_DB_VERSION = 1

//FirebaseDb Properties
const val EVENTS_BACKUP_COLLECTION = "EventsBackUp"


// Increase DB Version everytime any change is made to any table or a new table is added.
const val NUDGE_DATABASE = "NudgeDatabase"
const val NUDGE_DATABASE_VERSION = 2
const val NUDGE_GRANT_DATABASE = "NudgeGrantDatabase"

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
const val PDF_MIME_TYPE = "application/pdf"
const val EXCEL_TYPE = "text/csv"
const val PDF_TYPE = "text/pdf"
const val REGENERATE_PREFIX = "regenerate_"
const val SOMETHING_WENT_WRONG="Something Went Wrong"
const val PREF_KEY_IS_SETTING_SCREEN_OPEN= "is_setting_open"
const val ENGLISH_LANGUAGE_CODE="en"
const val UPCM_USER="Ultra Poor change maker (UPCM)"
const val SUFFIX_IMAGE_ZIP_FILE = "Image"
const val SUFFIX_EVENT_ZIP_FILE = "file"
const val BASELINE = "BASELINE-GRANT"
const val HAMLET = "HAMLET"
const val SELECTION = "SELECTION"

const val ZIP_EXTENSION = "zip"
const val EVENT_STRING="event"
const val FAILED_EVENT_STRING = "failed"

const val DEFAULT_LANGUAGE_NAME = "English"
const val DEFAULT_LANGUAGE_LOCAL_NAME = "English"
const val KOKBOROK_LANGUAGE_CODE="ky"
const val IMAGE_EVENT_STRING = "IMAGE"

// Header Constant
const val KEY_HEADER_AUTH = "authorization"
const val AUTH_TOKEN_PREFIX = "Bearer"
const val KEY_HEADER_TYPE = "header_type"
const val KEY_HEADER_MOBILE = "header_type_mobile"
const val MULTIPART_FORM_DATA = "multipart/form-data"
const val MULTIPART_IMAGE_PARAM_NAME = "imageFile"

const val SUCCESS = "SUCCESS"
const val FAIL = "FAIL"

const val SUCCESS_CODE = "200"

const val DEFAULT_LANGUAGE_CODE = "en"
const val DEFAULT_LANGUAGE_ID = 2

const val ATTENDANCE_TAG_ID = 94
const val DEFAULT_DATE_RANGE_DURATION: Long = 30

const val ATTENDANCE_PRESENT = "Present"
const val ATTENDANCE_ABSENT = "Absent"
const val ATTENDANCE_DELETED = "Deleted"
const val DD_MMM_YYYY_FORMAT = "dd MMM, yyyy"

const val SMALL_GROUP_ATTENDANCE_MISSION = "SMALL_GROUP_ATTENDANCE"

val eventWriters = listOf<IEventWriter>(
    TextFileEventWriter(),
    DbEventWrite(),
    LogEventWriter(),
    ImageEventWriter()
)
const val BATCH_DEFAULT_LIMIT = 5
const val RETRY_DEFAULT_COUNT = 3
const val SYNC_DATE_TIME_FORMAT = "yyyy-MM-dd"
const val SYNC_VIEW_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss a"
const val LAST_SYNC_TIME = "last_sync_time"