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

        fun getOrdinalFromStep (step: String): Int {
            return when (step) {
                NOT_STARTED.name -> NOT_STARTED.ordinal
                INPROGRESS.name -> INPROGRESS.ordinal
                COMPLETED.name -> COMPLETED.ordinal
                else -> NOT_STARTED.ordinal
            }
        }
    }

}