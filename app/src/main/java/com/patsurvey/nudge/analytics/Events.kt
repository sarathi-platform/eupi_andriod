package com.patsurvey.nudge.analytics

enum class Events(val eventName: String) {

    API_FAILED("api_failed"),
    LOCATION_ENABLED("location_enabled"),
    LOCATION_FETCH_FAILED("location_fetch_failed"),
    LOCATION_PERMISSION_GRANTED("location_permission_granted"),
    LOCATION_FETCHED("location_fetched"),

}