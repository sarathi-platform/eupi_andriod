package com.patsurvey.nudge.utils

enum class PatSurveyStatus {
    NOT_STARTED,
    INPROGRESS,
    COMPLETED,
    NOT_AVAILABLE,
    NOT_AVAILABLE_WITH_CONTINUE
    NOT_AVAILABLE;

    companion object {
        fun toInt(status: String) : Int {
            return when(status) {
                NOT_AVAILABLE.name -> NOT_AVAILABLE.ordinal
                INPROGRESS.name -> INPROGRESS.ordinal
                COMPLETED.name -> COMPLETED.ordinal
                else -> NOT_STARTED.ordinal
            }
        }
    }
}

enum class SHGFlag(val value: Int) {
    YES(1),
    NO(2),
    NOT_MARKED(-1);

    companion object {
        fun fromInt(downloadState: Int) : SHGFlag {
            return when(downloadState) {
                YES.ordinal -> YES
                NO.ordinal -> NO
                else -> NOT_MARKED
            }
        }
    }

    override fun toString(): String {
        return when(this) {
            YES -> "YES"
            NO -> "NO"
            NOT_MARKED -> "NOT MARKED"
        }
    }

}

enum class VoEndorsementStatus {
    NOT_STARTED,
    INPROGRESS,
    COMPLETED
}