package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.IMATStatusEventRepository

class MATStatusEventWriterUseCase(
    private val repository: IMATStatusEventRepository,
    private val eventWriterRepositoryImpl: EventWriterRepositoryImpl
) {
    suspend fun updateTaskStatus(
        taskEntity: ActivityTaskEntity, surveyName: String, subjectType: String
    ) {

        val saveAnswerEventDto = repository.writeTaskStatusEvent(
            taskEntity = taskEntity, subjectType = subjectType
        )
        eventWriterRepositoryImpl.createAndSaveEvent(
            saveAnswerEventDto, EventName.TASKS_STATUS_EVENT, EventType.STATEFUL, surveyName
        )?.let {

            eventWriterRepositoryImpl.saveEventToMultipleSources(
                it, listOf(), EventType.STATEFUL
            )
        }
    }

    suspend fun updateActivityStatus(
        activityEntity: ActivityEntity,
        surveyName: String,
    ) {

        val saveAnswerEventDto = repository.writeActivityStatusEvent(
            activityEntity
        )
        eventWriterRepositoryImpl.createAndSaveEvent(
            saveAnswerEventDto,
            EventName.ACTIVITIES_STATUS_EVENT,
            EventType.STATEFUL,
            surveyName
        )?.let {

            eventWriterRepositoryImpl.saveEventToMultipleSources(
                it, listOf(), EventType.STATEFUL
            )


        }
    }

    suspend fun markMATStatus(
        missionId: Int,
        activityId: Int,
        taskId: Int,
        surveyName: String,
        subjectType: String
    ) {
        updateMissionStatus(repository.getMissionEntity(missionId), surveyName)
        repository.getActivityEntity(missionId = missionId, activityId = activityId)
            ?.let { updateActivityStatus(it, surveyName) }
        updateTaskStatus(
            repository.getTaskEntity(taskId),
            surveyName = surveyName,
            subjectType = subjectType
        )
    }

    suspend fun updateMissionStatus(missionId: Int, surveyName: String) {
        updateMissionStatus(repository.getMissionEntity(missionId), surveyName)
    }

    suspend fun updateActivityStatus(missionId: Int, activityId: Int, surveyName: String) {
        repository.getActivityEntity(missionId = missionId, activityId = activityId)
            ?.let { updateActivityStatus(it, surveyName) }
    }


    suspend fun updateMissionStatus(
        missionEntity: MissionEntity,
        surveyName: String
    ) {

        val saveAnswerEventDto = repository.writeMissionStatusEvent(missionEntity)
        eventWriterRepositoryImpl.createAndSaveEvent(
            saveAnswerEventDto,
            EventName.MISSIONS_STATUS_EVENT,
            EventType.STATEFUL,
            surveyName = surveyName
        )?.let {

            eventWriterRepositoryImpl.saveEventToMultipleSources(
                it, listOf(), EventType.STATEFUL
            )


        }
    }


}