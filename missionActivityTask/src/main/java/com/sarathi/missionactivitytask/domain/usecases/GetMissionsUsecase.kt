package com.sarathi.missionactivitytask.domain.usecases

import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.missionactivitytask.domain.repository.GetMissionRepositoryImpl
import javax.inject.Inject

class GetMissionsUseCase @Inject constructor(private val missionRepositoryImpl: GetMissionRepositoryImpl) {


    suspend fun getAllMission(): List<MissionUiModel> = missionRepositoryImpl.getAllActiveMission()

}