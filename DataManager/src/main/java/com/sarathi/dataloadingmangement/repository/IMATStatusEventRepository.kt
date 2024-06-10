package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.model.events.UpdateActivityStatusEventDto
import com.sarathi.dataloadingmangement.model.events.UpdateMissionStatusEventDto
import com.sarathi.dataloadingmangement.model.events.UpdateTaskStatusEventDto

interface IMATStatusEventRepository {

    suspend fun writeTaskStatusEvent(
        taskEntity: ActivityTaskEntity,
        subjectType: String
    ): UpdateTaskStatusEventDto

    suspend fun writeActivityStatusEvent(
        activityID: ActivityEntity
    ): UpdateActivityStatusEventDto

    suspend fun writeMissionStatusEvent(
        missionEntity: MissionEntity
    ): UpdateMissionStatusEventDto

}