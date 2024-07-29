package com.nudge.core.enums

import com.nudge.core.BLANK_STRING

enum class EventName(val id: Int, val depends_on: List<Int> = emptyList(), val topicName: String) {

    ADD_TOLA(1, topicName = "COHORT_SAVE_TOPIC"),
    UPDATE_TOLA(2, listOf(1), topicName = "COHORT_SAVE_TOPIC"),
    DELETE_TOLA(3, listOf(2, 1), topicName = "COHORT_DELETE_TOPIC"),
    ADD_DIDI(4, listOf(2, 1), topicName = "BENEFICIARY_SAVE_TOPIC"),
    UPDATE_DIDI(5, listOf(4), topicName = "BENEFICIARY_SAVE_TOPIC"),
    DELETE_DIDI(6, listOf(5, 4), topicName = "BENEFICIARY_DELETE_TOPIC"),
    SAVE_WEALTH_RANKING(7, listOf(5, 4, 1), "BENEFICIARY_EDIT_TOPIC"),
    SAVE_PAT_ANSWERS(8, listOf(7, 4, 1), topicName = "PAT_SAVE_TOPIC"),
    INPROGRESS_PAT_SCORE(9, listOf(7, 4, 1), topicName = "BENEFICIARY_EDIT_TOPIC"),
    COMPLETED_PAT_SCORE(10, listOf(9, 7, 4, 1), topicName = "BENEFICIARY_EDIT_TOPIC"),
    REJECTED_PAT_SCORE(11, listOf(9, 7, 4, 1), topicName = "BENEFICIARY_EDIT_TOPIC"),
    NOT_AVAILBLE_PAT_SCORE(18, listOf(7, 4, 1), topicName = "BENEFICIARY_EDIT_TOPIC"),
    SAVE_VO_ENDORSEMENT(12, listOf(9, 4, 7, 1), topicName = "BENEFICIARY_EDIT_TOPIC"),
    CRP_IMAGE(13, listOf(5, 4), topicName = "IMAGE_TOPIC"),
    BPC_IMAGE(13, listOf(5, 4), topicName = "IMAGE_TOPIC"),
    FORM_A_TOPIC(14, listOf(12), topicName = "FORM_A_TOPIC"),
    FORM_B_TOPIC(14, listOf(12), topicName = "FORM_B_TOPIC"),
    FORM_C_TOPIC(14, listOf(12), topicName = "FORM_C_TOPIC"),
    FORM_D_TOPIC(14, listOf(12), topicName = "FORM_D_TOPIC"),
    SAVE_BPC_MATCH_SCORE(15, emptyList(), topicName = "BPC_TOPIC"),
    WORKFLOW_STATUS_UPDATE(16, emptyList(), "WORKFLOW_TOPIC"),
    RANKING_FLAG_EDIT(17, emptyList(), "RANKING_FLAG_EDIT_TOPIC"),
    ADD_SECTION_PROGRESS_FOR_DIDI_EVENT(19, topicName = "ADD_SECTION_PROGRESS_FOR_DIDI_EVENT"),
    UPDATE_SECTION_PROGRESS_FOR_DIDI_EVENT(
        20,
        topicName = "UPDATE_SECTION_PROGRESS_FOR_DIDI_EVENT"
    ),
    SAVE_RESPONSE_EVENT(21, topicName = "SAVE_RESPONSE_EVENT"),
    UPDATE_TASK_STATUS_EVENT(22, topicName = "UPDATE_TASK_STATUS_EVENT"),
    UPDATE_ACTIVITY_STATUS_EVENT(23, topicName = "UPDATE_ACTIVITY_STATUS_EVENT"),
    UPDATE_MISSION_STATUS_EVENT(24, topicName = "UPDATE_MISSION_STATUS_EVENT"),
    UPLOAD_IMAGE_RESPONSE_EVENT(25, topicName = "UPLOAD_IMAGE_RESPONSE_EVENT"),
    MONEY_JOURNAL_EVENT(26, topicName = "GRANT_ACTIVITY_TOPIC"),
    MISSIONS_STATUS_EVENT(27, topicName = "MISSION_ACTIVITY_TASK_STATUS_TOPIC"),
    ACTIVITIES_STATUS_EVENT(28, topicName = "MISSION_ACTIVITY_TASK_STATUS_TOPIC"),
    TASKS_STATUS_EVENT(29, topicName = "MISSION_ACTIVITY_TASK_STATUS_TOPIC"),
    GRANT_SAVE_RESPONSE_EVENT(30, topicName = "SAVE_RESPONSE_EVENT"),
    GRANT_DELETE_RESPONSE_EVENT(31, topicName = "SAVE_RESPONSE_EVENT"),
    UPDATE_FORM_DETAILS_EVENT(32, topicName = "GRANT_ACTIVITY_TOPIC"),
    UPLOAD_DOCUMENT_EVENT(33, topicName = "DOCUMENT_TOPIC"),
    SAVE_SUBJECT_ATTENDANCE_EVENT(34, topicName = "SMALL_GROUP_ATTENDANCE_TOPIC"),
    DELETE_SUBJECT_ATTENDANCE_EVENT(35, topicName = "SMALL_GROUP_ATTENDANCE_TOPIC"),
    FORM_RESPONSE_EVENT(36, topicName = "SAVE_RESPONSE_EVENT");

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