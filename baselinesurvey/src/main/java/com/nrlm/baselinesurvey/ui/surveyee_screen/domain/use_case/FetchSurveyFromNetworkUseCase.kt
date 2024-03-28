package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import android.util.Log
import com.nrlm.baselinesurvey.SUCCESS_CODE
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.json

class FetchSurveyFromNetworkUseCase(
    private val repository: DataLoadingScreenRepository
) {
    suspend operator fun invoke(surveyRequestBodyModel: SurveyRequestBodyModel): Boolean {
        try {


            /*
            //TODO Run a loop on language id later
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
            Log.d("TAG", "invoke fetchSurveyFromNetwork: ${surveyRequestBodyModel.json()}")
            val surveyApiResponse = repository.fetchSurveyFromNetwork(surveyRequestBodyModel)
            if (surveyApiResponse.status.equals(
                    SUCCESS_CODE,
                    true
                )
            ) {
                surveyApiResponse.data?.let { surveyApiResponse ->
//                    for (survey in surveyApiResponse) {
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
        } catch (ex: Exception) {
            BaselineLogger.e("FetchSurveyFromNetworkUseCase", "invoke", ex)
            return false
        }
    }

    suspend fun getLanguages(): List<LanguageEntity> {
        return repository.fetchLocalLanguageList()
    }

    fun getStateId(): Int {
        return repository.getStateId()
    }
}
