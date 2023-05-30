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