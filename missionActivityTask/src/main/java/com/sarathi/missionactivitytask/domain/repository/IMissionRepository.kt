package com.sarathi.missionactivitytask.domain.repository

import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel


interface IMissionRepository {
    suspend fun getAllActiveMission(): List<MissionUiModel>
}