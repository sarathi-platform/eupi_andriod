package com.sarathi.dataloadingmangement.domain.use_case

import com.google.android.gms.common.api.ApiException
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.model.survey.request.SurveyRequest
import com.sarathi.dataloadingmangement.repository.ISurveyDownloadRepository


class FetchSurveyDataFromNetworkUseCase(
    private val repository: ISurveyDownloadRepository,
) {
    suspend operator fun invoke(): Boolean {
        try {

            val apiResponse = repository.fetchSurveyFromNetwork(
                SurveyRequest(
                    referenceId = 31,
                    referenceType = "STATE",
                    surveyId = 3
                )
            )
            if (apiResponse.status.equals(SUCCESS_CODE, true)) {
                apiResponse.data?.let { surveyApiResponse ->
                    repository.saveSurveyToDb(surveyApiResponse)

                    return true
                }
            } else {
                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
        return false
    }

}