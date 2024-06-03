package com.sarathi.surveymanager.utils.events

sealed class EventWriterEvents {
    data class SaveAnswerEvent(val subjectId: String)

}