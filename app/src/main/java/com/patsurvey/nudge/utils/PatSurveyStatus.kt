package com.patsurvey.nudge.utils

enum class PatSurveyStatus {
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

enum class SHGFlag(val value: Int) {
    YES(1),
    NO(2),
    NOT_MARKED(-1);

    companion object {
        fun fromInt(downloadState: Int) : SHGFlag {
            return when(downloadState) {
                YES.value -> YES
                NO.value -> NO
                else -> NO
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