package com.nrlm.baselinesurvey.data.domain

import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SurveyState
import com.nudge.core.database.entities.Events

interface EventWriterHelper {

    suspend fun createUpdateSectionStatusEvent(
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        sectionStatus: SectionStatus
    ): Events

    suspend fun createSaveAnswerEvent(
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        questionId: Int,
        questionType: String,
        saveAnswerEventOptionItemDtoList: List<SaveAnswerEventOptionItemDto>
    ): Events

    /*suspend fun creteSubjectStatusUpdateEvent(
        surveyId: Int,
        subjectId: Int,
        status: SectionStatus
     ): Events
*/

    suspend fun createTaskStatusUpdateEvent(
        subjectId: Int,
        sectionStatus: SurveyState
    ): Events
}