package com.sarathi.dataloadingmangement.domain

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.enums.ApiStatus
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS
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
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(screenName, triggerType, moduleName, customData)) {
                return false
            }
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
                    updateApiCallStatus(
                        screenName = screenName,
                        moduleName = moduleName,
                        triggerType = triggerType,
                        status = ApiStatus.SUCCESS.name,
                        customData = customData,
                        errorMsg = BLANK_STRING
                    )
                    return true
                }
            } else {
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = customData,
                    errorMsg = apiResponse.message
                )
                return false
            }

        } catch (apiException: ApiException) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = customData,
                errorMsg = apiException.stackTraceToString()
            )
            throw apiException
        } catch (ex: Exception) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = customData,
                errorMsg = ex.stackTraceToString()
            )
            throw ex
        }
        return false
    }

    fun getAllMission(): Flow<List<MissionUiModel>> {
        return repository.getAllMission()
    }

    override fun getApiEndpoint(): String {
        return SUB_PATH_GET_MISSION_DETAILS
    }

    suspend fun isMissionLoaded(missionId: Int, programId: Int): Int {
        return repository.isMissionLoaded(missionId, programId)
    }

    suspend fun setMissionLoaded(missionId: Int, programId: Int) {
        return repository.setMissionLoaded(missionId, programId)
    }

    suspend fun fetchMissionInfo(missionId: Int): MissionInfoUIModel? {
        return repository.fetchMissionInfo(missionId)
    }
}