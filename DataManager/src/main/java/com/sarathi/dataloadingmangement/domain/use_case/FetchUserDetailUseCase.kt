package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.analytics.AnalyticsManager
import com.nudge.core.database.entities.language.LanguageEntity
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.repository.IUserDetailRepository
import javax.inject.Inject

class FetchUserDetailUseCase @Inject constructor(
    private val repository: IUserDetailRepository,
    private val analyticsManager: AnalyticsManager
) {
    suspend fun invoke(): Boolean {
        try {
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
                return true
            } else {
                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
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

}