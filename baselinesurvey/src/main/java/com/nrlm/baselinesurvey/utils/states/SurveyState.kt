package com.nrlm.baselinesurvey.utils.states

enum class SurveyState {
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

        fun getStatusFromOrdinal(surveyState: Int) : SurveyState {
            return when (surveyState) {
                NOT_AVAILABLE.ordinal -> NOT_AVAILABLE
                INPROGRESS.ordinal -> INPROGRESS
                COMPLETED.ordinal -> COMPLETED
                NOT_AVAILABLE_WITH_CONTINUE.ordinal -> NOT_AVAILABLE_WITH_CONTINUE
                else -> NOT_STARTED
            }
        }
    }
}

fun Array<SurveyState>.toStringList(): List<String> {
    val stringList = mutableListOf<String>()
    this.forEach {
        stringList.add(it.name)
    }
    return stringList
}