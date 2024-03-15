package com.nrlm.baselinesurvey.ui.common_components.common_events

import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.states.SectionStatus

sealed class EventWriterEvents {

    data class UpdateSectionStatusEvent(
        val surveyId: Int,
        val sectionId: Int,
        val didiId: Int,
        val sectionStatus: SectionStatus
    ) : EventWriterEvents()

    data class SaveAnswerEvent(
        val surveyId: Int,
        val sectionId: Int,
        val didiId: Int,
        val questionId: Int,
        val questionType: String,
        val questionTag: Int,
        val showConditionalQuestion: Boolean = true,
        val saveAnswerEventOptionItemDtoList: List<SaveAnswerEventOptionItemDto>
    )

    data class UpdateConditionalAnswerEvent(
        val surveyId: Int,
        val sectionId: Int,
        val didiId: Int,
        val questionId: Int,
        val saveAnswerEventOptionItemDtoList: List<SaveAnswerEventOptionItemDto>
    )

    data class UpdateSubjectSurveyStatus(
        val missionId: Int,
        val activityId: Int,
        val surveyId: Int,
        val subjectId: Int,
        val status: SectionStatus
    )

    data class UpdateTaskStatusEvent(
        val subjectId: Int,
        val status: SectionStatus
    )

    data class UpdateActivityStatusEvent(
        val missionId: Int,
        val activityId: Int,
        val status: SectionStatus
    )

    data class UpdateMissionStatusEvent(
        val missionId: Int,
        val status: SectionStatus
    )

    data class UpdateMissionActivityTaskStatus(
        val missionId: Int,
        val activityId: Int,
        val taskId: Int,
        val status: SectionStatus
    )

}