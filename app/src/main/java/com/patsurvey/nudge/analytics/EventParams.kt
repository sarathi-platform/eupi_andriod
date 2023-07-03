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
    PERMISSION_GRANTED("permission_granted"),
    LOCATION_ENABLED("location_enabled"),
    LOCATION_MODE("location_mode"),
    LOCATION_MODE_INT("location_mode_int"),
    LOCATION_CRITERIA("location_criteria"),
    LOCATION_CRITERIA_ACCURACY("location_criteria_accuracy"),
    LOCATION_CRITERIA_POWER("location_criteria_power"),
    LOCATION_PROVIDER("location_provider"),
    BUILD_DEVICE("Build_DEVICE"),
    BUILD_MANUFACTURER("Build_MANUFACTURER"),
    BUILD_MODEL("Build_MODEL"),
    BUILD_BRAND("Build_BRAND"),



}