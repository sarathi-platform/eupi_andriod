package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS_CODE
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.network.ApiException
import com.nrlm.baselinesurvey.network.SUBPATH_FETCH_SURVEY_FROM_NETWORK
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nudge.core.enums.ApiStatus

class FetchSurveyFromNetworkUseCase(
    private val repository: DataLoadingScreenRepository
) {
    suspend operator fun invoke(surveyRequestBodyModel: SurveyRequestBodyModel): Boolean {
        try {


            /*//TODO Run a loop on language id later
            var surveyResponseModel: SurveyResponseModel? = null
            val testSurvey =
                BaselineCore.getAppContext().resources.openRawResource(R.raw.survey).use {
                    surveyResponseModel =
                        Gson().fromJson(it.reader(), SurveyResponseModel::class.java)
                }
            if (surveyResponseModel != null) {
                repository.saveSurveyToDb(surveyResponseModel!!, surveyRequestBodyModel.languageId)
                return true
            } else {
                return false
            }*/
            if (!repository.isNeedToCallApi(SUBPATH_FETCH_SURVEY_FROM_NETWORK)) {
                return false
            }
            repository.insertApiStatus(SUBPATH_FETCH_SURVEY_FROM_NETWORK)

            val surveyApiResponse = repository.fetchSurveyFromNetwork(surveyRequestBodyModel)
            if (surveyApiResponse.status.equals(
                    SUCCESS_CODE,
                    true
                )
            ) {
                 surveyApiResponse.data?.let { surveyApiResponse ->
 //                    for (survey in surveyApiResponse) {
                     repository.updateApiStatus(
                         SUBPATH_FETCH_SURVEY_FROM_NETWORK,
                         status = ApiStatus.SUCCESS.ordinal,
                         "",
                         200
                     )

                     repository.saveSurveyToDb(
                         surveyApiResponse,
                         languageId = surveyRequestBodyModel.languageId,
                     )
 //                    }
                     return true
                 }
                 return false
             } else {
                 return false
            }
        } catch (apiException: ApiException) {
            repository.updateApiStatus(
                SUBPATH_FETCH_SURVEY_FROM_NETWORK,
                status = ApiStatus.FAILED.ordinal,
                apiException.message ?: "",
                apiException.getStatusCode()
            )
            return false
        } catch (ex: Exception) {
            repository.updateApiStatus(
                SUBPATH_FETCH_SURVEY_FROM_NETWORK,
                status = ApiStatus.FAILED.ordinal,
                ex.message ?: "",
                500
            )
            BaselineLogger.e("FetchUserDetailFromNetworkUseCase", "invoke", ex)
            return false
        }
    }

    suspend fun getLanguages(): List<LanguageEntity> {
        return repository.fetchLocalLanguageList()
    }

    fun getAppLanguageId(): Int {
        return repository.getAppLanguageId()
    }

    fun getStateId(): Int {
        return repository.getStateId()
    }
}
