package com.nudge.core.analytics.mixpanel

enum class AnalyticsEvents(val eventName: String) {

    SYNC_STARTED("sync_started"),
    SYNC_SUCCESS("sync_success"),
    SYNC_API_FAILURE("sync_api_failure"),
    SYNC_FAILED_DUE_TO_EXCEPTION("sync_failed_due_to_exception"),
}


enum class AnalyticsEventsParam(val eventParam: String) {


    SYNC_TYPE("sync_type"),
    TOTAL_PENDING_EVENT_COUNT("total_pending_event_count"),
    SYNC_BATCH_SIZE("sync_batch_size"),
    CONNECTION_QUALITY("connection_quality"),
    RETRY_COUNT("retry_count"),
    API_FAILURE_TYPE("api_failure_type"),
    API_FAILURE_ERROR_MESSAGE("api_failure_error_message"),
    FAILED_EVENT_ID_LIST("failed_event_id_list"),
    EXCEPTION_MESSAGE("exception_message"),
    STACK_TRACE("stack_trace"),

}