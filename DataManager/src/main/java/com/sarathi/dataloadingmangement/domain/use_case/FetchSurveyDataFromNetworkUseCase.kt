package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.enums.ApiStatus
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_STATE_ID
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.STATE
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.model.survey.request.SurveyRequest
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_FETCH_SURVEY_FROM_NETWORK
import com.sarathi.dataloadingmangement.repository.ISurveyDownloadRepository
import javax.inject.Inject


class FetchSurveyDataFromNetworkUseCase @Inject constructor(
    private val repository: ISurveyDownloadRepository,
    private val sharedPrefs: CoreSharedPrefs,
    private val activityConfigDao: ActivityConfigDao,
    apiCallJournalRepository: IApiCallJournalRepository,
) : BaseApiCallNetworkUseCase(apiCallJournalRepository) {

    override suspend operator fun invoke(
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
            activityConfigDao.getSurveyIds(missionId, sharedPrefs.getUniqueUserIdentifier())
                .forEach { surveyId ->
                    callSurveyApi(
                        screenName = screenName,
                        moduleName = moduleName,
                        triggerType = triggerType,
                        customData = customData,
                        surveyRequest = SurveyRequest(
                            referenceId = getReferenceId(),
                            referenceType = STATE,
                            surveyId = surveyId
                        )
                    )
            }
        } catch (ex: Exception) {
            CoreLogger.e(
                tag = "TAG",
                msg = "invoke: Exception -> ${ex.message}",
                ex = ex,
                stackTrace = true
            )
            throw ex
        }
        return false
    }

    private fun getReferenceId() = sharedPrefs.getPref(PREF_STATE_ID, 31)

    private suspend fun callSurveyApi(
        surveyRequest: SurveyRequest,
        screenName: String,
        moduleName: String,
        triggerType: DataLoadingTriggerType,
        customData: Map<String, Any>
    ): Boolean {
        try {
            val apiResponse = repository.fetchSurveyFromNetwork(surveyRequest)
            if (apiResponse.status.equals(SUCCESS_CODE, true)) {
                apiResponse.data?.let { surveyApiResponse ->
                    repository.saveSurveyToDb(surveyApiResponse)
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
            CoreLogger.e(
                tag = "TAG",
                msg = "invoke: ApiException -> ${apiException.message}",
                ex = apiException,
                stackTrace = true
            )
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
            CoreLogger.e(
                tag = "TAG",
                msg = "invoke: Exception -> ${ex.message}",
                ex = ex,
                stackTrace = true
            )
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
        return SUBPATH_FETCH_SURVEY_FROM_NETWORK
    }
}
