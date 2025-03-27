package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_STATE_ID
import com.nudge.core.utils.CoreLogger
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
) : BaseApiCallNetworkUseCase() {

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
                        SurveyRequest(
                            referenceId = getReferenceId(),
                            referenceType = STATE,
                            surveyId = surveyId
                        )
                    )
            }
            return false
        } catch (apiException: ApiException) {
            CoreLogger.e(
                tag = "TAG",
                msg = "invoke: ApiException -> ${apiException.message}",
                ex = apiException,
                stackTrace = true
            )
            throw apiException
        } catch (ex: Exception) {
            CoreLogger.e(
                tag = "TAG",
                msg = "invoke: Exception -> ${ex.message}",
                ex = ex,
                stackTrace = true
            )
            throw ex
        }
    }

    private fun getReferenceId() = sharedPrefs.getPref(PREF_STATE_ID, 31)

    private suspend fun callSurveyApi(surveyRequest: SurveyRequest): Boolean {
        val apiResponse = repository.fetchSurveyFromNetwork(surveyRequest)
        if (apiResponse.status.equals(SUCCESS_CODE, true)) {
            apiResponse.data?.let { surveyApiResponse ->
                repository.saveSurveyToDb(surveyApiResponse)
                return true
            }
        } else {
            return true
        }
        return false
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_FETCH_SURVEY_FROM_NETWORK
    }
}
