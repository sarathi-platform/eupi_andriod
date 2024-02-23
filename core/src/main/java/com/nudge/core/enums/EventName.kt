package com.nudge.core.enums

import com.nudge.core.BLANK_STRING

enum class EventName(val id: Int, val depends_on: List<Int> = emptyList(), val topicName: String) {

    ADD_TOLA(1, topicName = "COHORT_SAVE_TOPIC"),
    UPDATE_TOLA(2, listOf(1), topicName = "COHORT_SAVE_TOPIC"),
    DELETE_TOLA(3, listOf(1), topicName = "COHORT_DELETE_TOPIC"),
    ADD_DIDI(4, listOf(2, 1), topicName = "BENEFICIARY_SAVE_TOPIC"),
    UPDATE_DIDI(5, listOf(4), topicName = "BENEFICIARY_SAVE_TOPIC"),
    DELETE_DIDI(6, listOf(4), topicName = "BENEFICIARY_DELETE_TOPIC"),
    SAVE_WEALTH_RANKING(7, listOf(5, 4, 1), "BENEFICIARY_EDIT_TOPIC"),
    SAVE_PAT_ANSWERS(8, listOf(7, 4, 1), topicName = "PAT_SAVE_TOPIC"),
    SAVE_PAT_SCORE(9, listOf(7, 4, 1), topicName = "BENEFICIARY_EDIT_TOPIC"),
    SAVE_VO_ENDORSEMENT(10, listOf(9, 4, 7, 1), topicName = "BENEFICIARY_EDIT_TOPIC"),
    CRP_IMAGE(11, listOf(4), topicName = "CRP_IMAGE_TOPIC"),
    BPC_IMAGE(11, listOf(4), topicName = "BPC_IMAGE_TOPIC"),
    FORM_A_TOPIC(12, listOf(10), topicName = "FORM_A_TOPIC"),
    FORM_B_TOPIC(12, listOf(10), topicName = "FORM_B_TOPIC"),
    FORM_C_TOPIC(12, listOf(10), topicName = "FORM_C_TOPIC"),
    FORM_D_TOPIC(12, listOf(10), topicName = "FORM_D_TOPIC"),
    SAVE_BPC_MATCH_SCORE(
        13,
        emptyList(),
        topicName = "BPC_TOPIC"
    ), //TODO GET TOPIC NAME FOR THIS EVENT
    WORKFLOW_STATUS_UPDATE(14, emptyList(), "WORKFLOW_TOPIC"),
    RANKING_FLAG_EDIT(15, emptyList(), "RANKING_FLAG_EDIT_TOPIC");
}

fun String.getTopicFromName(): String {
    return EventName.values().find { it.name == this }?.topicName ?: BLANK_STRING
}

fun EventName.getDependsOnEventNameForEvent(): List<EventName> {
    val dependentEventsName = mutableListOf<EventName>()
    this.depends_on.forEach { dependsOn ->
        val eventNames = EventName.values().filter { it.id == dependsOn }
        dependentEventsName.addAll(eventNames)
    }
    return dependentEventsName
}