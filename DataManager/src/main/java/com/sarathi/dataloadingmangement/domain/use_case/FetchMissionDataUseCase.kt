package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.model.uiModel.ActivityInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionInfoUIModel
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUB_PATH_GET_MISSION_DETAILS
import com.sarathi.dataloadingmangement.repository.IMissionRepository
import javax.inject.Inject

class FetchMissionDataUseCase @Inject constructor(
    private val repository: IMissionRepository,
) {
    suspend fun invoke(missionId: Int, programId: Int): Boolean {
        try {
            val startTime = System.currentTimeMillis()

            val apiResponse = repository.fetchActivityDataFromServer(programId, missionId)
            CoreLogger.d(
                tag = "LazyLoadAnalysis",
                msg = "FetchMissionDataUseCase :/mission-service/activity/get/activity-details/${programId}/${missionId}/:  ${System.currentTimeMillis() - startTime}"
            )

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

                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "FetchMissionDataUseCase -activity details  ${System.currentTimeMillis() - startTime}"
                )

                return true

            } else {
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "FetchMissionDataUseCase -activity details  ${System.currentTimeMillis() - startTime}"
                )
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
            val startTime = System.currentTimeMillis()
            val apiResponse = repository.fetchMissionListFromServer()
            CoreLogger.d(
                tag = "LazyLoadAnalysis",
                msg = "MissionList :$SUB_PATH_GET_MISSION_DETAILS  : ${System.currentTimeMillis() - startTime}"
            )

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
                    CoreLogger.d(
                        tag = "LazyLoadAnalysis",
                        msg = "getAllMissionList :${System.currentTimeMillis() - startTime}"
                    )
                    return true
                }
            } else {
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "getAllMissionList :${System.currentTimeMillis() - startTime}"
                )
                return false
            }
            CoreLogger.d(
                tag = "LazyLoadAnalysis",
                msg = "getAllMissionList :${System.currentTimeMillis() - startTime}"
            )


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
    suspend fun fetchMissionInfo(missionId: Int): MissionInfoUIModel? {
        return repository.fetchMissionInfo(missionId)
    }

    suspend fun fetchActivityInfo(missionId: Int, activityId: Int): ActivityInfoUIModel? {
        return repository.fetchActivityInfo(missionId, activityId)
    }

    suspend fun getActivityTypesForMission(missionId: Int): List<String> =
        repository.getActivityTypesForMission(missionId = missionId)

}