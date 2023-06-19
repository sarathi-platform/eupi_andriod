package com.patsurvey.nudge.analytics

enum class EventParams(val eventParam: String) {
    EXCEPTION("exception"),
    ERRORCODE("errorCode"),
    ERRORNAME("errorName"),
    SERVICE_CALL_TYPE("api_type"),
    API_PATH("api_path"),
    TOKENS("tokens"),
    USER_NAME("user_name"),
    SDK_INT("device_os"),
    BUILD_VERSION_NAME("build_version_name"),


}