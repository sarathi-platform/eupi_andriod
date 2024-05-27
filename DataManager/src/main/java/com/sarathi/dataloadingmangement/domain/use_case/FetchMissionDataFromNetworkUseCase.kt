package com.sarathi.dataloadingmangement.domain.use_case

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sarathi.dataloadingmangement.R
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.repository.IDataLoadingScreenRepository

class FetchMissionDataFromNetworkUseCase(
    private val repository: IDataLoadingScreenRepository
) {
    suspend operator fun invoke(context: Context): Boolean {
        try {

            val apiResponse = repository.fetchMissionDataFromServer("en", "BASELINE")
//            if (apiResponse.status.equals("SUCCESS", true)) {
            //            apiResponse.data?.let { missionApiResponse ->

                    var dumyyResponse: List<MissionResponse>? = null
                    context.resources.openRawResource(R.raw.mission_response).use {
                        val listType = object : TypeToken<List<MissionResponse>>() {}.getType()

                        dumyyResponse = Gson().fromJson(it.reader(), listType)
                    }
                    repository.saveMissionToDB(dumyyResponse!!)
                    dumyyResponse?.forEach { mission ->
                        repository.saveMissionsActivityToDB(
                            missionId = mission.id,
                            activities = mission.activities
                        )
                        mission.activities.forEach { activity ->

                            repository.saveMissionsActivityTaskToDB(
                                missionId = mission.id,
                                activityId = activity.id,
                                subject = activity.activityConfig.subject,
                                activities = activity.taskResponses
                            )
                        }
                    }
            //       return true
            // }
            // return false
            // }
//            else {
//                return false
//            }
            return true
        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }

}