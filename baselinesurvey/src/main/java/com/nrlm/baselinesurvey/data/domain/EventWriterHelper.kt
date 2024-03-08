package com.nrlm.baselinesurvey.data.domain

import com.nrlm.baselinesurvey.model.datamodel.ActivityForSubjectDto
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.states.SectionStatus
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
        sectionStatus: SectionStatus
    ): Events

    suspend fun createActivityStatusUpdateEvent(
        missionId: Int,
        activityId: Int,
        status: SectionStatus
    ): Events

    suspend fun createMissionStatusUpdateEvent(
        missionId: Int,
        status: SectionStatus
    ): Events

    suspend fun markMissionInProgress(missionId: Int, status: SectionStatus)

    suspend fun markActivityInProgress(missionId: Int, activityId: Int, status: SectionStatus)

    suspend fun markTaskInProgress(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        status: SectionStatus
    )

    suspend fun markMissionActivityTaskInProgress(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        status: SectionStatus
    )

    suspend fun markMissionCompleted(missionId: Int, status: SectionStatus)

    suspend fun markActivityCompleted(missionId: Int, activityId: Int, status: SectionStatus)

    suspend fun markTaskCompleted(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        status: SectionStatus
    )

    suspend fun markMissionActivityTaskICompleted(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        status: SectionStatus
    )


    suspend fun getActivityFromSubjectId(subjectId: Int): ActivityForSubjectDto

}