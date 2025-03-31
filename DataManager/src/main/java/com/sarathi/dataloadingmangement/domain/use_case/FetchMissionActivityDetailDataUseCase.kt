package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.enums.ApiStatus
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.model.uiModel.ActivityInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUB_PATH_GET_ACTIVITY_DETAILS
import com.sarathi.dataloadingmangement.repository.IMissionActivityDetailRepository
import javax.inject.Inject

class FetchMissionActivityDetailDataUseCase @Inject constructor(
    private val repository: IMissionActivityDetailRepository,
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
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.SUCCESS.name,
                    customData = customData,
                    errorMsg = BLANK_STRING
                )

                return true

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
        return SUB_PATH_GET_ACTIVITY_DETAILS
    }

}