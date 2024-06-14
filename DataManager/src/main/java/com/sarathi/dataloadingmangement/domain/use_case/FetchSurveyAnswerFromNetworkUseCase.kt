package com.sarathi.dataloadingmangement.domain.use_case

import com.google.android.gms.common.api.ApiException
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.model.survey.request.GetSurveyAnswerRequest
import com.sarathi.dataloadingmangement.repository.ISurveySaveNetworkRepository
import javax.inject.Inject


class FetchSurveyAnswerFromNetworkUseCase @Inject constructor(
    private val repository: ISurveySaveNetworkRepository,
    private val sharedPrefs: CoreSharedPrefs,
) {
    suspend operator fun invoke(): Boolean {
        try {
            repository.getSurveyIds().forEach {
                callSurveAnsweryApi(
                    GetSurveyAnswerRequest(
                        surveyId = it,
                        mobileNumber = sharedPrefs.getMobileNo(),
                        userId = sharedPrefs.getUserName().toInt()
                    )
                )
            }


        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
        return true
    }


    private suspend fun callSurveAnsweryApi(surveyRequest: GetSurveyAnswerRequest): Boolean {
        val apiResponse = repository.getSurveyAnswerFromNetwork(surveyRequest)
        if (apiResponse.status.equals(SUCCESS_CODE, true)) {
            apiResponse.data?.let { surveyApiResponse ->
                repository.saveSurveyAnswerToDb(surveyApiResponse)
                return true
            }
        } else {
            return true
        }
        return false
    }

}
