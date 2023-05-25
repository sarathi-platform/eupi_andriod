package com.patsurvey.nudge.utils

enum class PatSurveyStatus {
    NOT_STARTED,
    INPROGRESS,
    COMPLETED,
    NOT_AVAILABLE
}

enum class SHGFlag(val value: Int) {
    YES(1),
    NO(2),
    NOT_MARKED(-1)
}