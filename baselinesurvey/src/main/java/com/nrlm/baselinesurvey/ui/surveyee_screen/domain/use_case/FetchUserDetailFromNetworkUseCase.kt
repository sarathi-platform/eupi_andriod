package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ERROR_CODE
import com.nrlm.baselinesurvey.DEFAULT_SUCCESS_CODE
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
                        BLANK_STRING,
                        DEFAULT_SUCCESS_CODE
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
                    userApiResponse.message,
                    DEFAULT_ERROR_CODE
                )
                false
            }
        } catch (apiException: ApiException) {
            repository.updateApiStatus(
                SUBPATH_USER_VIEW,
                status = ApiStatus.FAILED.ordinal,
                apiException.message ?: BLANK_STRING,
                apiException.getStatusCode()
            )
            throw apiException
        } catch (ex: Exception) {
            repository.updateApiStatus(
                SUBPATH_USER_VIEW,
                status = ApiStatus.FAILED.ordinal,
                ex.message ?: BLANK_STRING,
                DEFAULT_ERROR_CODE
            )
            BaselineLogger.e("FetchUserDetailFromNetworkUseCase", "invoke", ex)
            throw ex
        }
    }
}
