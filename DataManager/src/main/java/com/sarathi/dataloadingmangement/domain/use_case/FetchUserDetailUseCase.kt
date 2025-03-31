package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.analytics.AnalyticsManager
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.database.entities.language.LanguageEntity
import com.nudge.core.enums.ApiStatus
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_USER_VIEW
import com.sarathi.dataloadingmangement.repository.IUserDetailRepository
import javax.inject.Inject

class FetchUserDetailUseCase @Inject constructor(
    private val repository: IUserDetailRepository,
    private val analyticsManager: AnalyticsManager,
    apiCallJournalRepository: IApiCallJournalRepository
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
            val localLanguageList = repository.fetchLanguage()
            val userViewApiRequest = createMultiLanguageVillageRequest(localLanguageList)
            val apiResponse =
                repository.fetchUseDetailFromNetwork(userViewApiRequest = userViewApiRequest)
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let { userApiResponse ->
                    analyticsManager.setUserDetail(
                        name = userApiResponse.name ?: BLANK_STRING,
                        userType = userApiResponse.typeName ?: BLANK_STRING,
                        distinctId = repository.getUSerMobileNo(),
                        buildEnvironment = repository.getBuildEnv()
                    )
                    repository.saveUserDetails(userApiResponse)
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

    fun createMultiLanguageVillageRequest(localLanguageList: List<LanguageEntity>): String {
        var requestString: StringBuilder = StringBuilder()
        var request: String = "2"
        if (localLanguageList.isNotEmpty()) {
            localLanguageList.forEach {
                requestString.append("${it.id}-")
            }
        } else request = "2"
        if (requestString.contains("-")) {
            request = requestString.substring(0, requestString.length - 1)
        }
        return request
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_USER_VIEW
    }
}