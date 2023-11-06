package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.createMultiLanguageVillageRequest

class FetchUserDetailFromNetworkUseCase (
    private val repository: DataLoadingScreenRepository
) {
    suspend operator fun invoke(): Boolean {
        val localLanguageList = repository.fetchLocalLanguageList()
        val userViewApiRequest = createMultiLanguageVillageRequest(localLanguageList)
        val userApiResponse = repository.fetchUseDetialsFromNetwork(userViewApiRequest = userViewApiRequest)
        if (userApiResponse.status.equals(SUCCESS, true)) {
            if(userApiResponse.data != null) {
                repository.saveUserDetails(userApiResponse.data)
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }
}
