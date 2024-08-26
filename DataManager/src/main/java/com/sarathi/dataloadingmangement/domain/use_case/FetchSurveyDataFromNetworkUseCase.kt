package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_STATE_ID
import com.sarathi.dataloadingmangement.STATE
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.model.survey.request.SurveyRequest
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.repository.ISurveyDownloadRepository
import javax.inject.Inject


class FetchSurveyDataFromNetworkUseCase @Inject constructor(
    private val repository: ISurveyDownloadRepository,
    private val sharedPrefs: CoreSharedPrefs,
    private val activityConfigDao: ActivityConfigDao,
) {
    suspend operator fun invoke(missionId: Int): Boolean {
        try {
            activityConfigDao.getSurveyIds(missionId).forEach { surveyId ->
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
            throw apiException
        } catch (ex: Exception) {
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
}
