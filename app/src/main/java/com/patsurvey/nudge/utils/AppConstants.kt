package com.patsurvey.nudge.utils

import androidx.compose.ui.unit.dp

const val BLANK_STRING=""
const val DEFAULT_LANGUAGE_CODE="en"
const val MOBILE_NUMBER_LENGTH=10
const val ASSET_VALUE_LENGTH=8
const val MAXIMUM_RANGE=99999
const val OTP_LENGTH = 6
var OTP_RESEND_DURATION: Long = 30 * 1000
var EXPANSTION_TRANSITION_DURATION: Int = 450
const val POOR_STRING="Poor"
const val MEDIUM_STRING="Medium"
const val RICH_STRING="Rich"
const val KEY_HEADER_AUTH = "authorization"
const val AUTH_TOKEN_PREFIX = "Bearer"
const val KEY_HEADER_TYPE = "header_type"
const val KEY_HEADER_MOBILE = "header_type_mobile"
const val HEADER_TYPE_NONE = "header_none"
const val KEY_SOURCE_TYPE = "sourceApp"
const val KEY_SOURCE_STAGE = "stage"
const val KEY_SOURCE_UAT = "uat"
const val KEY_SOURCE_PROD = "prod"
const val ACCESS_TOKEN = "ACCESS_TOKEN"
const val PREF_MOBILE_NUMBER = "pref_mobile_number"
const val SUCCESS = "SUCCESS"
const val FAIL = "FAIL"
const val SPLASH_SCREEN_DURATION = 2000L
const val STEP_COMPLETION_DELAY = 900L
const val NUDGE_DATABASE = "NudgeDatabase"
const val VILLAGE_TABLE_NAME = "village_table"
const val LANGUAGE_TABLE_NAME = "language_table"
const val NUMERIC_TABLE_NAME = "numeric_table"
const val OPTION_TABLE_NAME = "option_table"
const val USER_TABLE_NAME = "user_table"
const val STEPS_LIST_TABLE = "step_list_table"
const val CASTE_TABLE = "caste_table"
const val ANSWER_TABLE = "ques_answer_table"
const val LAST_SELECTED_TOLA_TABLE = "last_selected_tola_table"
const val DIDI_TABLE = "didi_table"
const val TOLA_TABLE = "tola_table"
const val TOLA_COUNT = "tola_count"
const val DIDI_COUNT = "didi_count"
const val QUESTION_TABLE = "question_table"
const val TRAINING_VIDEO_TABLE = "training_video_table"
const val BPC_SUMMARY_TABLE = "bpc_summary_table"
const val BPC_SELECTED_DIDI_TABLE = "bpc_selected_didi_table"
const val BPC_NON_SELECTED_DIDI_TABLE = "bpc_non_selected_didi_table"
const val BPC_SCORE_PERCENTAGE_TABLE = "bpc_score_percentage_table"

const val EMPTY_TOLA_NAME = "NO TOLA"
const val NO_TOLA_TITLE = "NO TOLA"

const val ONLINE_STATUS = "online_status"
const val SEC_30_STRING="00:30"
const val ADD_DIDI_BLANK_ID=0

// Pref Constants
const val PREF_KEY_USER_NAME="key_user_name"
const val PREF_KEY_NAME="key_name"
const val PREF_KEY_EMAIL="key_email"
const val PREF_KEY_IDENTITY_NUMBER="key_identity_number"
const val PREF_KEY_PROFILE_IMAGE="profile_image"
const val PREF_KEY_ROLE_NAME = "role_name"
const val PREF_KEY_TYPE_NAME = "type_name"
const val PREF_KEY_VO_ENDORSEMENT_STATUS = "voEndorsementStatus"
const val SNACKBAR_TAG = "snackbar"
const val SNACKBAR_MESSAGE = "snackbarMessage"
const val ARG_MOBILE_NUMBER = "mobile"
const val ARG_PAGE_FROM = "from_page"
const val ARG_FROM_HOME = "from_home"
const val ARG_FROM_SETTING = "from_setting"
const val ARG_FROM_PROGRESS = "from_progress_screen"
const val ARG_FROM_PAT_SURVEY = "from_pat_survey"
const val ARG_VILLAGE_NAME = "villageName"
const val ARG_VILLAGE_ID = "villageId"
const val ARG_DIDI_DETAILS_ID = "didi_details_id"
const val ARG_STEP_ID = "stepId"
const val ARG_DIDI_ID = "didiId"
const val ARG_DIDI_STATUS = "didi_status"
const val ARG_SECTION_TYPE = "section_type"
const val ARG_IS_STEP_COMPLETE = "isStepComplete"
const val ARG_STEP_INDEX = "step_index"
const val ARG_COMPLETION_MESSAGE = "completion_message"
const val ARG_FROM_SCREEN = "fromScreen"
const val ARG_FROM_PAT_DIDI_LIST_SCREEN = "from_pat_didi_list_screen"
const val ARG_FROM_PAT_SUMMARY_SCREEN = "from_pat_summary_screen"
const val ARG_FROM_VO_ENDORSEMENT_SCREEN = "from_vo_endorsement_screen"
const val ARG_VIDEO_ID = "video_id"
const val PREF_FORM_PATH = "pref_form_path"
const val ARG_FORM_PATH = "form_path"
const val ARG_IMAGE_PATH = "image_path"
const val ARG_FOR_REPLACEMENT = "for_replacement"
const val FORM_C = "form_C"
const val FORM_D = "form_D"
const val PREF_FORM_C_PAGE_COUNT = "pref_form_c_page_count"
const val PREF_FORM_D_PAGE_COUNT = "pref_form_d_page_count"
const val USER_CRP = "CRP"
const val USER_CPR = "CPR"
const val USER_BPC = "BPC"

const val PREF_PROGRAM_NAME = "programName"
const val PREF_OPEN_FROM_HOME = "open_from_home"
const val PREF_WEALTH_RANKING_COMPLETION_DATE = "wealth_ranking_completion_date"
const val PREF_PAT_COMPLETION_DATE = "pat_completion_date"
const val PREF_BPC_PAT_COMPLETION_DATE = "bpc_pat_completion_date"
const val PREF_VO_ENDORSEMENT_COMPLETION_DATE = "vo_endorsement_completion_date"
const val VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_ = "vo_endorsement_complete_for_village_"
const val PREF_BPC_DIDI_LIST_SYNCED_FOR_VILLAGE_ = "pref_bpc_didi_list_sync_for_village_"
const val HUSBAND_STRING = "Husband"
const val INPROGRESS_STRING = "INPROGRESS"
const val COMPLETED_STRING = "COMPLETED"
const val PAT_SURVEY_CONSTANT = "PAT SURVEY"
const val PAT_SURVEY = "PAT_SURVEY"
const val BPC_SURVEY_CONSTANT = "BPC_SURVEY"
const val FLAG_WEIGHT = "weight"
const val FLAG_RATIO = "ratio"

const val TYPE_EXCLUSION = "EXCLUSION"
const val TYPE_INCLUSION = "INCLUSION"
const val LOW_SCORE = "LOW SCORE"
const val DIDI_REJECTED = "REJECTED"
const val DIDI_NOT_AVAILABLE= "NOT_AVAILABLE"
const val TYPE_RADIO_BUTTON = "RadioButton"

const val ANSWER_TYPE_YES = "OPTION_A"
const val ANSWER_TYPE_NO = "OPTION_B"


const val RESPONSE_CODE_SUCCESS = 200        // Success
const val CODE_SUCCESS = 0        // Success
const val RESPONSE_CODE_DUNNING_RECHARGE = 40033        // Success
const val RESPONSE_CODE_INVALID_EMAIL = 700006
const val RESPONSE_CODE_INVALID_FIRST_NAME = 20109
const val RESPONSE_CODE_UNEXPECTED = 444    // No Response
const val RESPONSE_CODE_SERVER_ERROR = 500 // Unknown Error
const val RESPONSE_CODE_UNAUTHORIZED = 401 // Unauthorized
const val RESPONSE_CODE_NETWORK_ERROR = 99
const val RESPONSE_CODE_DEACTIVATED = 403  // Forbidden
const val RESPONSE_CODE_NOT_FOUND = 404
const val RESPONSE_CODE_TIMEOUT = 408
const val RESPONSE_CODE_UN_PROCESSABLE_ENTITY = 429
const val RESPONSE_CODE_UNPROCESSABLE_ENTITY = 422 // Unprocessable Entity
const val RESPONSE_CODE_BAD_REQUEST = 400 // Bad Request
const val RESPONSE_CODE_CONFLICT = 409 // Conflict
const val RESPONSE_CODE_500 = 500
const val RESPONSE_CODE_BAD_GATEWAY = 502
const val RESPONSE_CODE_SERVICE_TEMPORARY_UNAVAILABLE = 503
const val RESPONSE_CODE_NO_DATA = 101 // Locally defined : No data
const val RESPONSE_CODE_DOWNGRADE_ERROR = 40019
const val RESPONSE_CODE_DOWNGRADE_ERROR2 = 100014
const val RESPONSE_CODE_DOWNGRADE_ERROR3 = 3007
const val RESPONSE_CODE_PASSWORD_ERROR = 6034
const val RESPONSE_CODE_LOW_BALANCE_ERROR = 100030
const val RESPONSE_CODE_OTP_ERROR = 100014
const val RESPONSE_CODE_RATE_LIMIT = 429
const val ERROR_CODE_CONCURRENCY = 130401
const val ERROR_CODE_SUBSCRIBER_NOT_FOUND = 20022
const val ERROR_CODE_JSON_SYNTAX_ERROR = 10000
const val HTTP_EXCEPTION = "HttpException"
const val SOCKET_TIMEOUT_EXCEPTION = "SocketTimeoutException"
const val IO_EXCEPTION = "IOException"
const val JSON_SYNTAX_EXCEPTION = "JsonSyntaxException"
const val API_FAILED_EXCEPTION = "ApiResponseFailException"
const val UNKNOWN_EXCEPTION = "UnknownException"

val UNREACHABLE_ERROR_MSG =
    "There seems to be a problem accessing details on this screen. Please try again."
val GENERIC_ERROR_MSG =
    "Oops! An error has occurred on our server. Please check internet connection and try to playback again!"
const val NETWORK_ERROR_MSG = "Make sure that Wi-Fi or mobile data is turned on, then try again."
const val COMMON_ERROR_MSG = "Network Error, no network available."
const val COMMON_ERROR_TITLE = "Something Went Wrong"
const val TIMEOUT_ERROR_MSG = "Your request timed out. Please try again in some time."
val UNAUTHORISED_MESSAGE = "401 Unauthorized"

const val LAST_UPDATE_TIME = "last_updated_time"
const val LAST_SYNC_TIME = "last_sync_time"

const val SYNC_FAILED = "Online Sync Failed"
const val SYNC_SUCCESSFULL = "Online Sync Successful"

const val PREF_DIDI_UNAVAILABLE = "pref_didi_unavailable"
private val FabSize = 56.dp
val ExtendedFabSize = 48.dp
private val ExtendedFabIconPadding = 12.dp
private val ExtendedFabTextPadding = 20.dp

const val ACCEPTED = "ACCEPTED"
const val FORM_A_PDF_NAME = "Digital_Form_A"
const val FORM_B_PDF_NAME = "Digital_Form_B"
const val FORM_C_PDF_NAME = "Digital_Form_C"

const val PREF_RETRY_API_LIST = "pref_retry_api_list"
const val PREF_VILLAGE_ID_TO_RETRY = "pref_village_id_to_retry"
const val PREF_LANGUAGE_ID_TO_RETRY = "pref_language_id_to_retry"

const val BPC_USER_TYPE = "Project Coordinator"
const val CRP_USER_TYPE = "Community Resource Person"
const val ARG_USER_TYPE = "user_type"

const val MATCH_PERCENTAGE = 70
const val EXTENSION_WEBP = "webp"
const val QUESTION_FLAG_RATIO = "ratio"
const val QUESTION_FLAG_WEIGHT = "weight"
const val NUDGE_IMAGE_FOLDER = "nudge_images"

const val SENDER_NUMBER = "TM-DYNRLM"
const val PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ = "NEED_TO_POST_BPC_MATCH_SCORE_FOR_"


