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

        fun fromIntToString(status: Int): String {
            return when (status) {
                0 -> NOT_STARTED.name
                1 -> REJECTED.name
                2, 3 -> ACCEPTED.name
                else -> BLANK_STRING
            }
        }
    }

}

enum class EndorsementValue {
    NOT_SELECTED,
    REJECTED,
    ENDORSED;
}