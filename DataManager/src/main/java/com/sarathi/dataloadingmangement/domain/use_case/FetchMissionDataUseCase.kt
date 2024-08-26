package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.repository.IMissionRepository
import javax.inject.Inject

class FetchMissionDataUseCase @Inject constructor(
    private val repository: IMissionRepository,
) {
    suspend fun invoke(missionId: Int, programId: Int): Boolean {
        try {

            val apiResponse = repository.fetchActivityDataFromServer(programId, missionId)
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let { activityApiResponse ->

                    activityApiResponse.forEach { activity ->
                        repository.saveActivityConfig(
                            missionId = missionId,
                            missionActivityModel = activity,
                        )
                        repository.saveMissionsActivityTaskToDB(
                            missionId = missionId,
                            activityId = activity.id,
                            subject = activity.activityConfig?.subject ?: BLANK_STRING,
                            activities = activity.taskResponses ?: listOf()
                        )
                    }
                }

                return true

            } else {
                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun getAllMission(): List<MissionUiModel> {
        return repository.getAllMission()
    }

    suspend fun getAllMissionList(): Boolean {
        try {

            val apiResponse = repository.fetchMissionListFromServer()
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

}