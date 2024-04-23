package com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.network.ApiException
import com.nrlm.baselinesurvey.network.SUBPATH_GET_MISSION
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nudge.core.enums.ApiStatus
import kotlinx.coroutines.delay

class FetchMissionDataFromNetworkUseCase(
    private val repository: DataLoadingScreenRepository
) {
    suspend operator fun invoke(): Boolean {
        try {
            if (!repository.isNeedToCallApi(SUBPATH_GET_MISSION)) {
                return false
            }
            repository.insertApiStatus(SUBPATH_GET_MISSION)

            val apiResponse = repository.fetchMissionDataFromServer("en", "BASELINE")
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let { missionApiResponse ->
                    repository.updateApiStatus(
                        SUBPATH_GET_MISSION,
                        status = ApiStatus.SUCCESS.ordinal,
                        BLANK_STRING,
                        DEFAULT_SUCCESS_CODE
                    )
                    repository.deleteMissionsFromDB()
                    repository.deleteMissionActivitiesFromDB()
                    repository.deleteActivityTasksFromDB()
                    missionApiResponse.forEach { mission ->
                        var activityTaskSize = 0
                        mission.activities.forEach { activity ->
                            repository.saveMissionsActivityToDB(
                                MissionActivityEntity.getMissionActivityEntity(
                                    userId = repository.getBaseLineUserId(),
                                    missionId = mission.missionId,
                                    activity = activity,
                                    activityTaskSize = activity.tasks.size
                                )
                            )
                            activity.tasks.forEach { task ->
                                if (task.id != null) {
                                    repository.saveActivityTaskToDB(
                                        ActivityTaskEntity.getActivityTaskEntity(
                                            userId = repository.getBaseLineUserId(),
                                            missionId = mission.missionId,
                                            activityId = activity.activityId,
                                            activityName = activity.activityName,
                                            task = task,
                                        )
                                    )
                                }
                            }
                            activityTaskSize += activity.tasks.size
                        }
                        delay(100)
                        repository.saveMissionToDB(
                            MissionEntity.getMissionEntity(
                                userId = repository.getBaseLineUserId(),
                                activityTaskSize = activityTaskSize,
                                mission = mission
                            )
                        )
                    }
                    return true
                }
                return false
            } else {
                repository.updateApiStatus(
                    SUBPATH_GET_MISSION,
                    status = ApiStatus.FAILED.ordinal,
                    apiResponse.message ?: BLANK_STRING,
                    DEFAULT_ERROR_CODE
                )
                return false
            }
        } catch (apiException: ApiException) {
            repository.updateApiStatus(
                SUBPATH_GET_MISSION,
                status = ApiStatus.FAILED.ordinal,
                apiException.message ?: BLANK_STRING,
                apiException.getStatusCode()
            )
            throw apiException
        } catch (ex: Exception) {
            repository.updateApiStatus(
                SUBPATH_GET_MISSION,
                status = ApiStatus.FAILED.ordinal,
                ex.message ?: BLANK_STRING,
                DEFAULT_ERROR_CODE
            )
            BaselineLogger.e("FetchUserDetailFromNetworkUseCase", "invoke", ex)
            throw ex
        }
    }

}