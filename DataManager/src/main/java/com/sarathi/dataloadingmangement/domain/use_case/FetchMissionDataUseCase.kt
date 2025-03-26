package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.model.uiModel.ActivityInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUB_PATH_GET_MISSION_DETAILS
import com.sarathi.dataloadingmangement.repository.IMissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchMissionDataUseCase @Inject constructor(
    private val repository: IMissionRepository,
) : BaseApiCallNetworkUseCase() {

    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(screenName, triggerType, customData)) {
                return false
            }
            val missionId = customData["MissionId"] as Int
            val programId = customData["ProgramId"] as Int

            val apiResponse = repository.fetchActivityDataFromServer(programId, missionId)
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let { activityApiResponse ->

                    activityApiResponse.forEach { activity ->
                        repository.saveActivityConfig(
                            missionId = missionId,
                            missionActivityModel = activity,
                        )
                        repository.saveActivityOrderStatus(
                            missionId = missionId,
                            activityId = activity.id,
                            order = activity.order ?: 1
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

    fun getAllMission(): Flow<List<MissionUiModel>> {
        return repository.getAllMission()
    }
    suspend fun fetchMissionInfo(missionId: Int): MissionInfoUIModel? {
        return repository.fetchMissionInfo(missionId)
    }

    suspend fun fetchActivityInfo(missionId: Int, activityId: Int): ActivityInfoUIModel? {
        return repository.fetchActivityInfo(missionId, activityId)
    }

    suspend fun getActivityTypesForMission(missionId: Int): List<String> =
        repository.getActivityTypesForMission(missionId = missionId)

    override fun getApiEndpoint(): String {
        return SUB_PATH_GET_MISSION_DETAILS
    }

}