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
    APP_CONFIG_LOG_FILE("app_config_log_file"),
    CATCHED_EXCEPTION("catch_exception"),
    SQL_INJECTION("sql_injection"),
    DATA_SYNC_EVENT_PROGRESS("data_sync_event_progress"),
    IMAGE_SYNC_EVENT_PROGRESS("image_sync_event_progress"),
    OLD_DELETE_EVENT_COUNT("old_event_delete_count"),
    APP_LAUNCHED("app_launched"),
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
    EXCEPTION("exception"),

    SQL_INJECTION_STATUS("sql_injection_status"),
    SQL_INJECTION_MESSAGE("sql_injection_message"),

    OPEN_EVENT_COUNT("open_event_count"),
    PRODUCER_IN_PROGRESS_EVENT_COUNT("producer_in_progress_event_count"),
    PRODUCER_SUCCESS_EVENT_COUNT("producer_success_event_count"),
    PRODUCER_FAILED_EVENT_COUNT("producer_failed_event_count"),
    CONSUMER_IN_PROGRESS_EVENT_COUNT("consumer_in_progress_event_count"),
    CONSUMER_SUCCESS_EVENT_COUNT("consumer_success_event_count"),
    CONSUMER_FAILED_EVENT_COUNT("consumer_failed_event_count"),
    IMAGE_NOT_EXIST_EVENT_COUNT("image_not_exists_event_count"),
    BLOB_UPLOAD_FAILED_EVENT_COUNT("blob_upload_failed_event_count"),
    TOTAL_EVENT_COUNT("total_event_count"),

    TOTAL_DELETED_EVENT_COUNT("total_deleted_event_count"),
    BUILD_ENVIRONMENT_NAME("build_environment_name"),

    TOTAL_STORAGE("total_storage"),
    FREE_STORAGE("free_storage"),
    APP_SIZE("app_size"),
    DATA_SIZE("data_size"),
    CACHE_SIZE("cache_size"),
    SELECTED_LANGUAGE("selected_language"),

    PARAM_MOBILE_NUMBER("mobile_number"),
    PARAM_IP_ADDRESS("ip_address"),
}

data class CommonEventParams(
    val batchLimit: Int,
    val retryCount: Int,
    val connectionQuality: String
)