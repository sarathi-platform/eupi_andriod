package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.nudge.core.DEFAULT_SUCCESS_CODE
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_SMALL_GROUP_MAPPING
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupDetailsFromNetworkRepository
import javax.inject.Inject

class FetchSmallGroupFromNetworkUseCase @Inject constructor(
    private val fetchSmallGroupDetailsFromNetworkRepository: FetchSmallGroupDetailsFromNetworkRepository,
    apiCallJournalRepository: IApiCallJournalRepository
) : BaseApiCallNetworkUseCase(apiCallJournalRepository) {

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
            return fetchSmallGroupDetailsFromNetworkRepository.fetchSmallGroupDetails()
        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }
    suspend fun isFetchSmallGroupDetailsAPIFailed(): Boolean {
        return fetchSmallGroupDetailsFromNetworkRepository.isFetchSmallGroupDetailsAPIStatus()?.status != DEFAULT_SUCCESS_CODE
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_GET_SMALL_GROUP_MAPPING
    }


}