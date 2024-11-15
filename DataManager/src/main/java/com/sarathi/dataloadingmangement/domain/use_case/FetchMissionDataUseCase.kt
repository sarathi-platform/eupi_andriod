package com.sarathi.dataloadingmangement.domain.use_case

import android.util.Log
import com.nudge.core.json
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
                        Log.d("TAG", "invokeActivytDatails: ${activity.json()} ")
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


    suspend fun isMissionLoaded(missionId: Int, programId: Int): Int {
        return repository.isMissionLoaded(missionId, programId)
    }

    suspend fun setMissionLoaded(missionId: Int, programId: Int) {
        return repository.setMissionLoaded(missionId, programId)
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

    suspend fun getAllMission(): List<MissionUiModel> {
        return repository.getAllMission()
    }

    suspend fun getActivityTypesForMission(missionId: Int): List<String> =
        repository.getActivityTypesForMission(missionId = missionId)

}