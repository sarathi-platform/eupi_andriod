package com.patsurvey.nudge.utils

enum class DidiEndorsementStatus {
    NOT_STARTED,
    REJECTED,
    ENDORSED,
    ACCEPTED;

    companion object {
        fun toInt(status: String): Int {
            return when(status) {
                ENDORSED.name, ACCEPTED.name -> ENDORSED.ordinal
                REJECTED.name -> REJECTED.ordinal
                else -> NOT_STARTED.ordinal
            }
        }
    }

}

enum class EndorsementValue {
    NOT_SELECTED,
    REJECTED,
    ENDORSED;
}