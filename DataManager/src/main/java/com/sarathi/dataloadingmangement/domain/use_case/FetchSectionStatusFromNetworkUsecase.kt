package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.GET_SECTION_STATUS
import com.sarathi.dataloadingmangement.repository.ISectionStatusRepository
import javax.inject.Inject

class FetchSectionStatusFromNetworkUsecase @Inject constructor(val sectionRepository: ISectionStatusRepository) :
    BaseApiCallNetworkUseCase() {

    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(screenName, triggerType, customData)) {
                return false
            }
            //TODO need to confirmation added MissionId on customData
            val missionId = customData["MissionId"] as Int
            sectionRepository.getActivityConfigForMission(missionId = missionId)
                ?.let { activityConfigList ->
                    for (config in activityConfigList) {
                        val apiResponse = sectionRepository.fetchSectionStatusFromNetwork(config)
                        if (apiResponse.message.equals(SUCCESS, true)) {
                            apiResponse.data?.let {
                                sectionRepository.saveSectionStatusIntoDb(
                                    missionId = missionId,
                                    sectionStatus = apiResponse.data!!
                                )
                            }
                        }
                }
            }
        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
        return true
    }

    override fun getApiEndpoint(): String {
        return GET_SECTION_STATUS
    }

}
