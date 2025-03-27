package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.nudge.core.DEFAULT_SUCCESS_CODE
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_DIDI_LIST
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchDidiDetailsFromNetworkRepository
import javax.inject.Inject

class FetchDidiDetailsFromNetworkUseCase @Inject constructor(
    private val fetchDidiDetailsFromNetworkRepository: FetchDidiDetailsFromNetworkRepository
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
            return fetchDidiDetailsFromNetworkRepository.fetchDidiDetailsFromNetwork()
        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun isFetchDidiDetailsAPIStatusFailed(): Boolean {
        return fetchDidiDetailsFromNetworkRepository.isFetchDidiDetailsAPIStatus()?.errorCode != DEFAULT_SUCCESS_CODE
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_GET_DIDI_LIST
    }

}