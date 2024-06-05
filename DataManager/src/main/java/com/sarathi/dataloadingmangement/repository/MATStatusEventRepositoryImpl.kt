package com.sarathi.dataloadingmangement.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.model.StatusReferenceType
import com.sarathi.dataloadingmangement.model.events.UpdateActivityStatusEventDto
import com.sarathi.dataloadingmangement.model.events.UpdateMissionStatusEventDto
import com.sarathi.dataloadingmangement.model.events.UpdateTaskStatusEventDto
import javax.inject.Inject

class MATStatusEventRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs
) : IMATStatusEventRepository {
    override suspend fun writeTaskStatusEvent(
        taskEntity: ActivityTaskEntity,
        subjectType: String
    ): UpdateTaskStatusEventDto {
        return UpdateTaskStatusEventDto(
            missionId = taskEntity.missionId,
            activityId = taskEntity.activityId,
            taskId = taskEntity.taskId,
            actualCompletedDate = taskEntity.actualCompletedDate,
            actualStartDate = taskEntity.actualStartDate,
            localTaskId = taskEntity.localTaskId,
            referenceType = StatusReferenceType.TASK.name,
            status = taskEntity.status ?: BLANK_STRING,
            subjectId = taskEntity.subjectId,
            subjectType = subjectType
        )
    }

    override suspend fun writeActivityStatusEvent(activityEntity: ActivityEntity): UpdateActivityStatusEventDto {
        return UpdateActivityStatusEventDto(
            missionId = activityEntity.missionId,
            activityId = activityEntity.activityId,
            actualStartDate = activityEntity.actualStartDate,
            completedDate = activityEntity.actualEndDate,
            referenceType = StatusReferenceType.ACTIVITY.name,
            status = activityEntity.status ?: BLANK_STRING
        )
    }

    override suspend fun writeMissionStatusEvent(missionEntity: MissionEntity): UpdateMissionStatusEventDto {
        return UpdateMissionStatusEventDto(
            missionId = missionEntity.missionId,
            actualStartDate = missionEntity.actualStartDate,
            completedDate = missionEntity.actualCompletedDate,
            referenceType = StatusReferenceType.MISSION.name,
            status = missionEntity.status
        )
    }


}