package com.patsurvey.nudge.utils

enum class DidiEndorsementStatus {
    NOT_STARTED,
    REJECTED,
    ENDORSED;

    companion object {
        fun toInt(status: String): Int {
            return when(status) {
                ENDORSED.name -> ENDORSED.ordinal
                REJECTED.name -> REJECTED.ordinal
                else -> NOT_STARTED.ordinal
            }
        }
    }

}