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
const val EVENT_STATUS_TABLE_NAME = "events_status_table"
const val EventDependencyTable = "event_dependency_table"
const val ApiStatusTable = "api_status_table"
const val IMAGE_STATUS_TABLE_NAME = "image_status_table"
const val REQUEST_STATUS_TABLE_NAME = "request_status_table"

// Sync DB Properties
const val SYNC_MANAGER_DATABASE = "SyncManagerDatabase"
const val SYNC_MANAGER_DB_VERSION = 2

//FirebaseDb Properties
const val EVENTS_BACKUP_COLLECTION = "EventsBackUp"


// Increase DB Version everytime any change is made to any table or a new table is added.
const val NUDGE_DATABASE = "NudgeDatabase"
const val NUDGE_DATABASE_VERSION = 3
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
const val IMAGE = "IMAGE"
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
const val CRP_USER_TYPE = "Community Resource Person"
const val SUFFIX_IMAGE_ZIP_FILE = "Image"
const val SUFFIX_EVENT_ZIP_FILE = "file"
const val BASELINE = "BASELINE_GRANT"
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
const val MULTIPART_IMAGE_PARAM_NAME = "files"

const val SUCCESS = "SUCCESS"
const val FAIL = "FAIL"

const val SUCCESS_CODE = "200"

const val DEFAULT_LANGUAGE_CODE = "en"
const val DEFAULT_LANGUAGE_ID = 2

const val ATTENDANCE_TAG_ID = 94
const val DEFAULT_DATE_RANGE_DURATION: Long = 30
const val WEEK_DURATION_RANGE: Long = 7

const val ATTENDANCE_PRESENT = "Present"
const val ATTENDANCE_ABSENT = "Absent"
const val ATTENDANCE_DELETED = "Deleted"
const val DD_MMM_YYYY_FORMAT = "dd MMM, yyyy"
const val MMM_DD_YYYY_FORMAT = "MMM dd, yyyy"

const val LIVELIHOOD = "Livelihood"
const val DIDI = "Didi"
const val DD_mmm_YY_FORMAT = "dd MMM, yy"
const val DD_mmm_hh_mm_FORMAT = "dd MMM, hh:mm"
const val DEFAULT_LIVELIHOOD_ID = -2

const val SMALL_GROUP_ATTENDANCE_MISSION = "SMALL_GROUP_ATTENDANCE"

val eventWriters = listOf<IEventWriter>(
    TextFileEventWriter(),
    DbEventWrite(),
    LogEventWriter(),
    ImageEventWriter()
)

var EXPANSTION_TRANSITION_DURATION: Int = 450
const val TRANSITION = "transition"
const val ANIMATE_COLOR = "animate color"
const val ROTATION_DEGREE_TRANSITION = "rotationDegreeTransition"

const val MAXIMUM_RANGE = 999999
const val MAXIMUM_RANGE_LENGTH = 10

const val NO_SG_FILTER_VALUE = "null"
const val NO_SG_FILTER_LABEL = "No Small Group Assigned"
const val BATCH_DEFAULT_LIMIT = 5
const val RETRY_DEFAULT_COUNT = 3
const val SYNC_DATE_TIME_FORMAT = "yyyy-MM-dd"
const val SYNC_VIEW_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss a"
const val LAST_SYNC_TIME = "last_sync_time"
const val FAILED_EVENTS_FILE = "Failed_Events_File"
const val SYNC_SELECTION_DRIVE = "selection"
const val SYNC_POST_SELECTION_DRIVE = "postSelection"
const val REMOTE_CONFIG_SYNC_ENABLE = "syncEnabled"
const val REMOTE_CONFIG_SYNC_OPTION_ENABLE = "isSyncDataEnabled"
const val REMOTE_CONFIG_SYNC_BATCH_SIZE = "sync_batch_size"
const val REMOTE_CONFIG_SYNC_RETRY_COUNT = "sync_retry_count"
const val REMOTE_CONFIG_MIX_PANEL_TOKEN = "mix_panel_token"

const val PRODUCER = "Producer"
const val CONSUMER = "consumer"
const val FORM_C_TOPIC = "FORM_C_TOPIC"
const val FORM_D_TOPIC = "FORM_D_TOPIC"
const val IMAGE_STRING = "image"
const val DATA_STRING = "data"
const val DATA_PRODUCER_STRING = "data_producer"
const val IMAGE_PRODUCER_STRING = "image_producer"
const val SYNC_IMAGE = "IMAGE"
const val SYNC_DATA = "DATA"

const val DEFAULT_BUILD_ENVIRONMENT = "UAT"
const val PREF_BUILD_ENVIRONMENT = "pref_build_environment"
const val PREF_MIX_PANEL_TOKEN = "pref_mix_panel_token"
const val REMOTE_CONFIG_SYNC_IMAGE_UPLOAD_ENABLE = "isImageBlobUploadEnable"
const val PREF_SYNC_IMAGE_UPLOAD_ENABLE = "pref_blob_upload_enable"
const val PREF_DATA_TAB_VISIBILITY = "data_tab_visibility"
const val PARENT_EVENT_NAME = "parent_event_name"
const val PARENT_TOPIC_NAME = "parent_topic_name"
const val FILE_PATH = "file_path"
const val CONTENT_TYPE = "content_type"
const val IS_ONLY_DATA = "isOnlyData"
const val DRIVE_TYPE = "driveType"
const val BLOB_URL = "blobUrl"
const val POST_SELECTION_CONTAINER = "uat/eupi-documents/content"
const val SELECTION_CONTAINER = "uat/eupi-documents"
