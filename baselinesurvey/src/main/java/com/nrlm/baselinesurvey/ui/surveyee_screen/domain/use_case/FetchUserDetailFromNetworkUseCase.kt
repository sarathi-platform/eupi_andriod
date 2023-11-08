package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.createMultiLanguageVillageRequest

class FetchUserDetailFromNetworkUseCase (
    private val repository: DataLoadingScreenRepository
) {
    suspend operator fun invoke(): Boolean {
        try {
            val localLanguageList = repository.fetchLocalLanguageList()
            val userViewApiRequest = createMultiLanguageVillageRequest(localLanguageList)
            val userApiResponse = repository.fetchUseDetialsFromNetwork(userViewApiRequest = userViewApiRequest)
            return if (userApiResponse.status.equals(SUCCESS, true)) {
                if(userApiResponse.data != null) {
                    repository.saveUserDetails(userApiResponse.data)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (ex: Exception) {
            BaselineLogger.e("FetchUserDetailFromNetworkUseCase", "invoke", ex)
            return false
        }
    }
}
