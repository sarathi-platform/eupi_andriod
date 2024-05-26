package com.sarathi.missionactivitytask.domain.repository

import com.sarathi.dataloadingmangement.data.entities.MissionEntity


interface IMissionRepository {
    suspend fun getAllActiveMission(): List<MissionEntity>
}