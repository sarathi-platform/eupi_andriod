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
        taskEntity: ActivityTaskEntity, surveyName: String, subjectType: String,
        isFromRegenerate: Boolean = false
    ) {

        val updateTaskStatusEventDto = repository.writeTaskStatusEvent(
            taskEntity = taskEntity, subjectType = subjectType
        )
        eventWriterRepositoryImpl.createAndSaveEvent(
            updateTaskStatusEventDto,
            EventName.TASKS_STATUS_EVENT,
            EventType.STATEFUL,
            surveyName,
            isFromRegenerate
        )?.let {

            eventWriterRepositoryImpl.saveEventToMultipleSources(
                it, listOf(), EventType.STATEFUL
            )
        }
    }

    suspend fun updateActivityStatus(
        activityEntity: ActivityEntity,
        surveyName: String,
        isFromRegenerate: Boolean

    ) {

        val saveAnswerEventDto = repository.writeActivityStatusEvent(
            activityEntity
        )
        eventWriterRepositoryImpl.createAndSaveEvent(
            saveAnswerEventDto,
            EventName.ACTIVITIES_STATUS_EVENT,
            EventType.STATEFUL,
            surveyName,
            isFromRegenerate
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
        subjectType: String,
        isFromRegenerate: Boolean = false
    ) {
        updateMissionStatus(repository.getMissionEntity(missionId), surveyName, isFromRegenerate)
        repository.getActivityEntity(missionId = missionId, activityId = activityId)
            ?.let { updateActivityStatus(it, surveyName, isFromRegenerate) }
        updateTaskStatus(
            getTaskEntity(taskId),
            surveyName = surveyName,
            subjectType = subjectType,
            isFromRegenerate
        )
    }

    suspend fun updateMissionStatus(missionId: Int, surveyName: String, isFromRegenerate: Boolean) {
        updateMissionStatus(
            repository.getMissionEntity(missionId),
            surveyName,
            isFromRegenerate = isFromRegenerate
        )
    }

    suspend fun updateActivityStatus(
        missionId: Int,
        activityId: Int,
        surveyName: String,
        isFromRegenerate: Boolean
    ) {
        repository.getActivityEntity(missionId = missionId, activityId = activityId)
            ?.let { updateActivityStatus(it, surveyName, isFromRegenerate) }
    }


    suspend fun updateMissionStatus(
        missionEntity: MissionEntity,
        surveyName: String,
        isFromRegenerate: Boolean
    ) {

        val saveAnswerEventDto = repository.writeMissionStatusEvent(missionEntity)
        eventWriterRepositoryImpl.createAndSaveEvent(
            saveAnswerEventDto,
            EventName.MISSIONS_STATUS_EVENT,
            EventType.STATEFUL,
            surveyName = surveyName,
            isFromRegenerate = isFromRegenerate
        )?.let {

            eventWriterRepositoryImpl.saveEventToMultipleSources(
                it, listOf(), EventType.STATEFUL
            )


        }
    }

    suspend fun getTaskEntity(taskId: Int): ActivityTaskEntity {
        return repository.getTaskEntity(taskId)
    }

}