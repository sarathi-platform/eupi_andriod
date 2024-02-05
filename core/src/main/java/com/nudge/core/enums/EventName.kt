package com.nudge.core.enums

import com.nudge.core.BLANK_STRING

enum class EventName(val id: Int, val depends_on: List<Int> = emptyList(), val topicName: String) {

    ADD_TOLA(1, topicName = "COHORT_SAVE_TOPIC"),
   // UPDATE_TOLA(2, listOf(1), topicName = "COHORT_EDIT_TOPIC"),
    DELETE_TOLA(3, listOf(1), topicName = "COHORT_DELETE_TOPIC"),
    ADD_DIDI(4, listOf(1), topicName = "BENEFICIARY_SAVE_TOPIC"),
   // UPDATE_DIDI(5, listOf(4), topicName = "BENEFICIARY_EDIT_TOPIC"),
    DELETE_DIDI(6, listOf(4), topicName = "BENEFICIARY_DELETE_TOPIC"),
    SAVE_WEALTH_RANKING(7, listOf(1, 4, 5), "BENEFICIARY_EDIT_TOPIC"),
    SAVE_PAT_ANSWERS(8, listOf(1, 4, 7), topicName = "PAT_SAVE_TOPIC"),
    SAVE_PAT_SCORE(9, listOf(1, 4, 7), topicName = "BENEFICIARY_EDIT_TOPIC"),
    SAVE_VO_ENDORSEMENT(10, listOf(1, 4, 7, 9), topicName = "BENEFICIARY_EDIT_TOPIC"),
    UPLOAD_DIDI_IMAGE(11, listOf(4), topicName = "UPLOAD_DIDI_IMAGE"),
    UPLOAD_FORM_IMAGE(12, listOf(10), topicName = "UPLOAD_FORM_IMAGE"),

    SAVE_BPC_MATCH_SCORE(13, emptyList(), topicName = "BPC_SAVE_MISMATCH_TOPIC"), //TODO GET TOPIC NAME FOR THIS EVENT
    WORKFLOW_STATUS_UPDATE(14, emptyList(), "WORKFLOW_UPDATE");

}

fun String.getTopicFromName(): String {
    return EventName.values().find { it.name == this }?.topicName ?: BLANK_STRING
}

fun EventName.getDependentEventNameForEvent(): List<EventName> {
    val dependentEventsName = mutableListOf<EventName>()
    this.depends_on.forEach { dependsOn ->
        val eventNames = EventName.values().filter { it.id == dependsOn }
        dependentEventsName.addAll(eventNames)
    }
    return dependentEventsName
}