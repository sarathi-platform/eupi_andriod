package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.network.ApiException
import com.nrlm.baselinesurvey.network.SUBPATH_USER_VIEW
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.createMultiLanguageVillageRequest
import com.nudge.core.enums.ApiStatus

class FetchUserDetailFromNetworkUseCase (
    private val repository: DataLoadingScreenRepository
) {
    suspend operator fun invoke(): Boolean {
        try {
            if (!repository.isNeedToCallApi(SUBPATH_USER_VIEW)) {
                return false
            }
            val localLanguageList = repository.fetchLocalLanguageList()
            val userViewApiRequest = createMultiLanguageVillageRequest(localLanguageList)
            repository.insertApiStatus(SUBPATH_USER_VIEW)
            val userApiResponse = repository.fetchUseDetialsFromNetwork(userViewApiRequest = userViewApiRequest)
            return if (userApiResponse.status.equals(SUCCESS, true)) {
                if(userApiResponse.data != null) {
                    repository.updateApiStatus(
                        SUBPATH_USER_VIEW,
                        status = ApiStatus.SUCCESS.ordinal,
                        "",
                        200
                    )
                    repository.saveUserDetails(userApiResponse.data)
                    true
                } else {
                    false
                }
            } else {
                repository.updateApiStatus(
                    SUBPATH_USER_VIEW,
                    status = ApiStatus.FAILED.ordinal,
                    userApiResponse.message ?: "",
                    500
                )
                false
            }
        } catch (apiException: ApiException) {
            repository.updateApiStatus(
                SUBPATH_USER_VIEW,
                status = ApiStatus.FAILED.ordinal,
                apiException.message ?: "",
                apiException.getStatusCode()
            )
            throw apiException
        } catch (ex: Exception) {
            repository.updateApiStatus(
                SUBPATH_USER_VIEW,
                status = ApiStatus.FAILED.ordinal,
                ex.message ?: "",
                500
            )
            BaselineLogger.e("FetchUserDetailFromNetworkUseCase", "invoke", ex)
            throw ex
        }
    }
}
