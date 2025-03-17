package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.model.survey.request.GetSurveyAnswerRequest
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_SURVEY_ANSWERS
import com.sarathi.dataloadingmangement.repository.ISurveySaveNetworkRepository
import javax.inject.Inject


class FetchSurveyAnswerFromNetworkUseCase @Inject constructor(
    private val repository: ISurveySaveNetworkRepository,
    private val sharedPrefs: CoreSharedPrefs,
) {
    suspend operator fun invoke(missionId: Int): Boolean {
        try {
            repository.getActivityConfig(missionId = missionId)?.forEach {

                callSurveAnsweryApi(
                    GetSurveyAnswerRequest(
                        referenceId = sharedPrefs.getStateId(),
                        surveyId = it.surveyId,
                        mobileNumber = sharedPrefs.getMobileNo(),
                        userId = sharedPrefs.getUserName().toInt(),
                        activityId = it.activityId
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
        val startTime = System.currentTimeMillis()
        val apiResponse = repository.getSurveyAnswerFromNetwork(surveyRequest)
        CoreLogger.d(
            tag = "LazyLoadAnalysis",
            msg = "callSurveAnsweryApi :$SUBPATH_SURVEY_ANSWERS/surveyId=${surveyRequest.surveyId}/activityId=${surveyRequest.activityId}  : ${System.currentTimeMillis() - startTime}"
        )

        if (apiResponse.status.equals(SUCCESS_CODE, true)) {
            apiResponse.data?.let { surveyApiResponse ->
                repository.saveSurveyAnswerToDb(surveyApiResponse)
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "SavecallSurveAnsweryApi :$SUBPATH_SURVEY_ANSWERS/surveyId=${surveyRequest.surveyId}/activityId=${surveyRequest.activityId}  : ${System.currentTimeMillis() - startTime}"
                )

                return true
            }

        } else {
            return true
        }
        return false
    }

}
