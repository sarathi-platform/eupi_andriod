package com.sarathi.dataloadingmangement.domain.use_case

import com.google.android.gms.common.api.ApiException
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.repository.IMissionRepository
import javax.inject.Inject

class FetchMissionDataUseCase @Inject constructor(
    private val repository: IMissionRepository,
) {
    suspend fun invoke(): Boolean {
        try {

            val apiResponse = repository.fetchMissionDataFromServer()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let { missionApiResponse ->

                    missionApiResponse.forEach { programme ->
                        repository.saveProgrammeToDb(programme)

                        repository.saveMissionToDB(programme.missions, programme.id)
                        programme.missions.forEach { mission ->
                            repository.saveMissionsActivityToDB(
                                missionId = mission.id,
                                activities = mission.activities
                            )
                            mission.activities.forEach { activity ->

                                repository.saveMissionsActivityTaskToDB(
                                    missionId = mission.id,
                                    activityId = activity.id,
                                    subject = activity.activityConfig?.subject ?: BLANK_STRING,
                                    activities = activity.taskResponses ?: listOf()
                                )
                            }
                        }
                    }
                    return true
                }
            } else {
                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
        return false
    }

    suspend fun getAllMission(): List<MissionUiModel> {
        return repository.getAllMission()
    }

}