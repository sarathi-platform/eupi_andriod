package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.enums.ApiStatus
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.model.survey.request.GetSurveyAnswerRequest
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_SURVEY_ANSWERS
import com.sarathi.dataloadingmangement.repository.ISurveySaveNetworkRepository
import javax.inject.Inject


class FetchSurveyAnswerFromNetworkUseCase @Inject constructor(
    private val repository: ISurveySaveNetworkRepository,
    private val sharedPrefs: CoreSharedPrefs,
) : BaseApiCallNetworkUseCase() {
    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(
                    screenName = screenName,
                    triggerType = triggerType,
                    moduleName = moduleName,
                    customData = customData,
                )
            ) {
                return false
            }
            //TODO need to add MissionId
            val missionId = customData["MissionId"] as Int
            repository.getActivityConfig(missionId = missionId)?.forEach {
                callSurveAnsweryApi(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    customData = customData,
                    surveyRequest = GetSurveyAnswerRequest(
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
        return false
    }


    private suspend fun callSurveAnsweryApi(
        surveyRequest: GetSurveyAnswerRequest,
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
            val apiResponse = repository.getSurveyAnswerFromNetwork(surveyRequest)
            if (apiResponse.status.equals(SUCCESS_CODE, true)) {
                apiResponse.data?.let { surveyApiResponse ->
                    repository.saveSurveyAnswerToDb(surveyApiResponse)
                }
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.SUCCESS.name,
                    customData = customData,
                    errorMsg = BLANK_STRING
                )
                return true
            } else {
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = customData,
                    errorMsg = apiResponse.message
                )
                return false
            }
        } catch (apiException: ApiException) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = customData,
                errorMsg = apiException.stackTraceToString()
            )
            throw apiException
        } catch (ex: Exception) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = customData,
                errorMsg = ex.stackTraceToString()
            )
            throw ex
        }
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_SURVEY_ANSWERS

    }

}
