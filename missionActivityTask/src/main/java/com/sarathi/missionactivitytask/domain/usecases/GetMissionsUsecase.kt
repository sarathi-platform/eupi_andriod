package com.sarathi.missionactivitytask.domain.usecases

import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.missionactivitytask.domain.repository.GetMissionRepositoryImpl
import javax.inject.Inject

class GetMissionsUseCase @Inject constructor(private val missionRepositoryImpl: GetMissionRepositoryImpl) {


    suspend fun getAllMission(): List<MissionEntity> = missionRepositoryImpl.getAllActiveMission()

}