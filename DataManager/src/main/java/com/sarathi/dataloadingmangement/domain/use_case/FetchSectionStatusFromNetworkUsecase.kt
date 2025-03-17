package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.GET_SECTION_STATUS
import com.sarathi.dataloadingmangement.repository.ISectionStatusRepository
import javax.inject.Inject

class FetchSectionStatusFromNetworkUsecase @Inject constructor(val sectionRepository: ISectionStatusRepository) {

    suspend fun invoke(missionId: Int): Boolean {
        try {

            sectionRepository.getActivityConfigForMission(missionId = missionId)
                ?.let { activityConfigList ->
                    for (config in activityConfigList) {
                        val startTime = System.currentTimeMillis()
                        val apiResponse = sectionRepository.fetchSectionStatusFromNetwork(config)
                        CoreLogger.d(
                            tag = "LazyLoadAnalysis",
                            msg = "FetchSectionStatusFromNetworkUsecase :$GET_SECTION_STATUS/${config.activityId}/:  ${System.currentTimeMillis() - startTime}"
                        )

                        if (apiResponse.message.equals(SUCCESS, true)) {
                            apiResponse.data?.let {
                                sectionRepository.saveSectionStatusIntoDb(
                                    missionId = missionId,
                                    sectionStatus = apiResponse.data!!
                                )
                            }
                        }
                        CoreLogger.d(
                            tag = "LazyLoadAnalysis",
                            msg = "FetchSectionStatusFromNetworkUsecase :$GET_SECTION_STATUS/${config.activityId}/:  ${System.currentTimeMillis() - startTime}"
                        )

                    }
            }
        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
        return true
    }

}
