package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_STATE_ID
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.STATE
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.model.survey.request.SurveyRequest
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.repository.ISurveyDownloadRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class FetchSurveyDataFromNetworkUseCase @Inject constructor(
    private val repository: ISurveyDownloadRepository,
    private val sharedPrefs: CoreSharedPrefs,
    private val activityConfigDao: ActivityConfigDao,
) {
    suspend operator fun invoke(missionId: Int): Boolean {
        try {
            coroutineScope {

                val deferredResults =
                    activityConfigDao.getSurveyIds(missionId, sharedPrefs.getUniqueUserIdentifier())
                        .map { surveyId ->
                            async {

                                callSurveyApi(
                        SurveyRequest(
                            referenceId = getReferenceId(),
                            referenceType = STATE,
                            surveyId = surveyId
                        )
                    )
                            }
                        }
                deferredResults.awaitAll()
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
}
