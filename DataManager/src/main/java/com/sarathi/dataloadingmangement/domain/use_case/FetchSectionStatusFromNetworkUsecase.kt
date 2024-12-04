package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.repository.ISectionStatusRepository
import javax.inject.Inject

class FetchSectionStatusFromNetworkUsecase @Inject constructor(val sectionRepository: ISectionStatusRepository) {

    suspend fun invoke(missionId: Int): Boolean {
        try {
            sectionRepository.getActivityConfigForMission(missionId = missionId)
                ?.distinctBy { it.surveyId }?.forEach {
                val apiResponse = sectionRepository.fetchSectionStatusFromNetwork(it)
                if (apiResponse.message.equals(SUCCESS, true)) {
                    apiResponse.data?.let {
                        sectionRepository.saveSectionStatusIntoDb(
                            missionId = missionId,
                            sectionStatus = apiResponse.data!!
                        )
                    }


                    return true
                } else {
                    return false
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
