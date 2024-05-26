package com.sarathi.missionactivitytask.domain.repository

import com.sarathi.dataloadingmangement.data.entities.Mission

interface IMissionRepository {
    suspend fun getAllActiveMission(): List<Mission>
}