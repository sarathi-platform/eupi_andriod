package com.nrlm.baselinesurvey.utils.states

enum class SectionStatus {
    NOT_STARTED,
    INPROGRESS,
    COMPLETED,
    NOT_AVAILABLE;

    companion object {
        fun getSectionStatusNameFromOrdinal(ordinal: Int): String {
            return when (ordinal) {
                0 -> NOT_STARTED.name
                1 -> INPROGRESS.name
                2 -> COMPLETED.name
                3 -> NOT_AVAILABLE.name
                else -> NOT_STARTED.name
            }
        }

        fun getOrdinalFromSectionStatus (step: String): Int {
            return when (step) {
                NOT_STARTED.name -> NOT_STARTED.ordinal
                INPROGRESS.name -> INPROGRESS.ordinal
                COMPLETED.name -> COMPLETED.ordinal
                NOT_AVAILABLE.name -> NOT_AVAILABLE.ordinal
                else -> NOT_STARTED.ordinal
            }
        }

        fun getSectionStatusFromOrdinal(ordinal: Int): SectionStatus {
            return when (ordinal) {
                0 -> NOT_STARTED
                1 -> INPROGRESS
                2 -> COMPLETED
                3 -> NOT_AVAILABLE
                else -> NOT_STARTED
            }
        }
    }
}

fun SectionStatus.toSurveyStatus(): SurveyState {
    return when (this) {
        SectionStatus.NOT_STARTED -> SurveyState.NOT_STARTED
        SectionStatus.INPROGRESS -> SurveyState.INPROGRESS
        SectionStatus.COMPLETED -> SurveyState.COMPLETED
        else -> SurveyState.NOT_STARTED
    }
}