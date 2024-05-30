package com.sarathi.dataloadingmangement.domain.use_case

import com.google.android.gms.common.api.ApiException
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.repository.IDataLoadingScreenRepository

class FetchMissionDataFromNetworkUseCase(
    private val repository: IDataLoadingScreenRepository
) {
    suspend operator fun invoke(): Boolean {
        try {

            val apiResponse = repository.fetchMissionDataFromServer()
            if (apiResponse.status.equals("SUCCESS", true)) {
                apiResponse.data?.let { missionApiResponse ->

//                    var dumyyResponse: List<ProgrameResponse>? = null
//                    context.resources.openRawResource(R.raw.mission_response).use {
//                        val listType = object : TypeToken<List<ProgrameResponse>>() {}.getType()
//
//                        dumyyResponse = Gson().fromJson(it.reader(), listType)
//                    }

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

}