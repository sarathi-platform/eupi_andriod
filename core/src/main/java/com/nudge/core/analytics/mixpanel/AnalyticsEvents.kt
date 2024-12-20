package com.nudge.core.analytics.mixpanel

enum class AnalyticsEvents(val eventName: String) {

    SYNC_STARTED("sync_started"),
    SYNC_SUCCESS("sync_success"),
    SYNC_PRODUCER_SUCCESS("sync_producer_success"),
    SYNC_CONSUMER_SUCCESS("sync_consumer_success"),
    SYNC_CONSUMER_FAILED("sync_consumer_failed"),
    SYNC_API_FAILURE("sync_api_failure"),
    SYNC_FAILED_DUE_TO_EXCEPTION("sync_failed_due_to_exception"),
    SYNC_CONSUMER_FAILED_DUE_TO_EXCEPTION("sync_consumer_failed_due_to_exception"),
    LOGIN("login"),
    LOGOUT("logout"),
    EXPORT_BACKUP_FILE("export_backup_file"),
    EXPORT_BACKUP_FILE_FAILED("export_backup_file_fail"),
    REGENERATE_ALL_EVENT("regenrate_all_event"),
    EXPORT_BASELINE_QNA("export_baseline_qna"),
    LOAD_SERVER_DATA("load_server_data"),
    IMPORT_DATA("import_data"),
    EXPORT_IMAGES("export_images"),
    EXPORT_EVENT_FILE("export_event_file"),
    EXPORT_DATABASE("export_database"),
    EXPORT_LOG_FILE("export_log_file"),
    CATCHED_EXCEPTION("catch_exception"),
}


enum class AnalyticsEventsParam(val eventParam: String) {

    SYNC_TYPE("sync_type"),

    CONSUMER_SUCCESS_REQUEST_IDS_COUNT("consumer_success_request_ids_count"),

    CONSUMER_FAIL_MESSAGE("consumer_fail_message"),
    CONSUMER_FAIL_REQUEST_IDS_COUNT("consumer_fail_request_ids_count"),

    TOTAL_PENDING_EVENT_COUNT("total_pending_event_count"),
    SYNC_BATCH_SIZE("sync_batch_size"),
    CONNECTION_QUALITY("connection_quality"),
    RETRY_COUNT("retry_count"),
    API_FAILURE_TYPE("api_failure_type"),
    API_FAILURE_ERROR_MESSAGE("api_failure_error_message"),
    FAILED_EVENT_ID_LIST("failed_event_id_list"),
    EXCEPTION_MESSAGE("exception_message"),
    STACK_TRACE("stack_trace"),
    EXCEPTION("exception")

}

data class CommonEventParams(
    val batchLimit: Int,
    val retryCount: Int,
    val connectionQuality: String
)