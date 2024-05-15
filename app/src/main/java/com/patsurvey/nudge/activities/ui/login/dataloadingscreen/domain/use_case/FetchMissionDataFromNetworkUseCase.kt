package com.patsurvey.nudge.activities.ui.login.dataloadingscreen.domain.use_case

import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.repository.IDataLoadingScreenRepository
import com.patsurvey.nudge.network.ApiException
import com.patsurvey.nudge.utils.SUCCESS

class FetchMissionDataFromNetworkUseCase(
    private val repository: IDataLoadingScreenRepository
) {
    suspend operator fun invoke(): Boolean {
        try {
            val apiResponse = repository.fetchMissionDataFromServer("en", "BASELINE")
            if (apiResponse.status.equals(SUCCESS, true)) {
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