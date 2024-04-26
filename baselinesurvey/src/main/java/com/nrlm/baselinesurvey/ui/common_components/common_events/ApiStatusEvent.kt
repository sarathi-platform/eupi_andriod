package com.nrlm.baselinesurvey.ui.common_components.common_events

sealed class ApiStatusEvent {
    data class showApiStatus(val errorCode: Int, val message: String) : DialogEvents()

}