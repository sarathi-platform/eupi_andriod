package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.model.survey.request.GetSurveyAnswerRequest
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.repository.ISurveySaveNetworkRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class FetchSurveyAnswerFromNetworkUseCase @Inject constructor(
    private val repository: ISurveySaveNetworkRepository,
    private val sharedPrefs: CoreSharedPrefs,
) {
    suspend operator fun invoke(missionId: Int): Boolean {
        try {
            coroutineScope {
                // Launch async calls for each survey ID
                val deferredResults = repository.getSurveyIds(missionId).map { surveyId ->
                    async {
                        callSurveAnsweryApi(
                            GetSurveyAnswerRequest(
                                surveyId = surveyId,
                                mobileNumber = sharedPrefs.getMobileNo(),
                                userId = sharedPrefs.getUserName().toInt()
                            )
                        )
                    }
                }

                // Await all calls to complete
                deferredResults.awaitAll()
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
