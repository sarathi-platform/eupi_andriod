package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.nudge.core.DEFAULT_SUCCESS_CODE
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.api.IApiJournalDatabaseRepository
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_SMALL_GROUP_MAPPING
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupDetailsFromNetworkRepository
import javax.inject.Inject

class FetchSmallGroupFromNetworkUseCase @Inject constructor(
    private val fetchSmallGroupDetailsFromNetworkRepository: FetchSmallGroupDetailsFromNetworkRepository,
    private val apiJournalDatabaseRepository: IApiJournalDatabaseRepository,
) : BaseApiCallNetworkUseCase() {
    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(screenName, triggerType, customData)) {
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