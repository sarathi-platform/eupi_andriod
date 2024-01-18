package com.nrlm.baselinesurvey.ui.mission_screen.domain.repository

import com.nrlm.baselinesurvey.model.datamodel.MissionResponseModel
import com.nrlm.baselinesurvey.model.request.MissionRequest
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import javax.inject.Inject

class MissionScreenRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : MissionScreenRepository {
    override suspend fun getMissions(): List<MissionResponseModel> {
        val missionRequest = MissionRequest("en", "Baseline")
        val getMissionToServerApiResponse =
            apiService.getBaseLineMission(missionRequest)
        return getMissionToServerApiResponse.data!!
    }
}