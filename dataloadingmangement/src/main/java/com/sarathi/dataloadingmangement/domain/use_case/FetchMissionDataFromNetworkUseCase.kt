package com.sarathi.dataloadingmangement.domain.use_case

import com.google.android.gms.common.api.ApiException
import com.sarathi.dataloadingmangement.repository.IDataLoadingScreenRepository

class FetchMissionDataFromNetworkUseCase(
    private val repository: IDataLoadingScreenRepository
) {
    suspend operator fun invoke(): Boolean {
        try {
            val apiResponse = repository.fetchMissionDataFromServer("en", "BASELINE")
            if (apiResponse.status.equals("SUCCESS", true)) {
                apiResponse.data?.let { missionApiResponse ->
                    repository.saveMissionToDB(missionApiResponse)
                    missionApiResponse.forEach { mission ->
                        repository.saveMissionsActivityToDB(
                            missionId = mission.missionId,
                            activities = mission.activities
                        )
                        mission.activities.forEach { activity ->

                            repository.saveMissionsActivityTaskToDB(
                                missionId = mission.missionId,
                                activityId = activity.activityId,
                                activityName = activity.activityName,
                                activities = activity.tasks
                            )
                        }
                    }
                    return true
                }
                return false
            } else {
                return false
            }
        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }

}