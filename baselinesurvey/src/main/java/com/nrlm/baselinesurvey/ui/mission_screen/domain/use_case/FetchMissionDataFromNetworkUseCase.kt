package com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger
import kotlinx.coroutines.delay

class FetchMissionDataFromNetworkUseCase(
    private val repository: DataLoadingScreenRepository
) {
    suspend operator fun invoke(): Boolean {
        try {
            val apiResponse = repository.fetchMissionDataFromServer("en", "BASELINE")
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let { missionApiResponse ->
                    repository.deleteMissionsFromDB()
                    repository.deleteMissionActivitiesFromDB()
                    repository.deleteActivityTasksFromDB()
                    missionApiResponse.forEach { mission ->
                        var activityTaskSize = 0
                        mission.activities.forEach { activity ->
                            repository.saveMissionsActivityToDB(
                                MissionActivityEntity.getMissionActivityEntity(
                                    missionId = mission.missionId,
                                    activity = activity,
                                    activityTaskSize = activity.tasks.size
                                )
                            )
                            activity.tasks.forEach { task ->
                                if (task.id != null) {
                                    repository.saveActivityTaskToDB(
                                        ActivityTaskEntity.getActivityTaskEntity(
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
                                activityTaskSize = activityTaskSize,
                                mission = mission
                            )
                        )
                    }
                    return true
                }
                return false
            } else {
                return false
            }
        } catch (ex: Exception) {
            BaselineLogger.e("FetchSurveyFromNetworkUseCase", "invoke", ex)
            return false
        }
    }

}