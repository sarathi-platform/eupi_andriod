package com.patsurvey.nudge.utils

enum class StepStatus {
    NOT_STARTED,
    INPROGRESS,
    COMPLETED;

    companion object {
        fun getStepFromOrdinal (ordinal: Int): String {
            return when (ordinal) {
                0 -> NOT_STARTED.name
                1 -> INPROGRESS.name
                2 -> COMPLETED.name
                else -> NOT_STARTED.name
            }
        }
    }

}