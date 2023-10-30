package com.nrlm.baselinesurvey.utils

enum class SurveyState {
    NOT_STARTED,
    INPROGRESS,
    COMPLETED,
    NOT_AVAILABLE,
    NOT_AVAILABLE_WITH_CONTINUE;
    companion object {
        fun toInt(status: String) : Int {
            return when(status) {
                NOT_AVAILABLE.name -> NOT_AVAILABLE.ordinal
                INPROGRESS.name -> INPROGRESS.ordinal
                COMPLETED.name -> COMPLETED.ordinal
                NOT_AVAILABLE_WITH_CONTINUE.name -> NOT_AVAILABLE_WITH_CONTINUE.ordinal
                else -> NOT_STARTED.ordinal
            }
        }
    }
}