package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS_CODE
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.network.ApiException
import com.nrlm.baselinesurvey.network.SUBPATH_FETCH_SURVEY_FROM_NETWORK
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger

class FetchSurveyFromNetworkUseCase(
    private val repository: DataLoadingScreenRepository
) {
    suspend operator fun invoke(surveyRequestBodyModel: SurveyRequestBodyModel): Boolean {
        try {
            if (!repository.isNeedToCallApi(SUBPATH_FETCH_SURVEY_FROM_NETWORK)) {
                return false
            }

            val surveyApiResponse = repository.fetchSurveyFromNetwork(surveyRequestBodyModel)
            if (surveyApiResponse.status.equals(
                    SUCCESS_CODE,
                    true
                )
            ) {
                surveyApiResponse.data?.let { surveyApiResponse ->
                    repository.saveSurveyToDb(
                        surveyApiResponse,
                        languageId = surveyRequestBodyModel.languageId,
                    )
                    return true
                }
                return false
            } else {
                return false
            }
        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            BaselineLogger.e("FetchUserDetailFromNetworkUseCase", "invoke", ex)
            throw ex
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
