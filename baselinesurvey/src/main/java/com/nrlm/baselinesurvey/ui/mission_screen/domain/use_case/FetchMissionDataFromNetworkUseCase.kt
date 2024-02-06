package com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger

class FetchMissionDataFromNetworkUseCase(
    private val repository: DataLoadingScreenRepository
) {
    suspend operator fun invoke(): Boolean {
        try {
            val apiResponse = repository.fetchMissionDataFromServer("en", "BASELINE")
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let { missionApiResponse ->
                    repository.deleteAllMissionToDB()
                    val missionsList = mutableListOf<MissionEntity>()
                    missionApiResponse.forEach { mission ->
                        val missionEntity = MissionEntity(
                            id = 0,
                            endDate = mission.endDate,
                            startDate = mission.startDate,
                            missionId = mission.missionId,
                            missionName = mission.missionName,
                            activities = mission.activities
                        )
                        missionsList.add(missionEntity)
                    }
                    repository.saveAllMissionToDB(missionsList);
                    return true
                }
                return false
            } else {
                return false
            }
        } catch (ex: Exception) {
            BaselineLogger.e("FetchSurveyFromNetworkUseCase", "invoke", ex)
            return false
        }
    }

}