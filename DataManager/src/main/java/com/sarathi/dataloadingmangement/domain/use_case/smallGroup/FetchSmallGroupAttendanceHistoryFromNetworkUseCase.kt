package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.nudge.core.DEFAULT_ERROR_CODE
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.api.IApiJournalDatabaseRepository
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupAttendanceHistoryFromNetworkRepository
import javax.inject.Inject

class FetchSmallGroupAttendanceHistoryFromNetworkUseCase @Inject constructor(
    private val fetchSmallGroupAttendanceHistoryFromNetworkRepository: FetchSmallGroupAttendanceHistoryFromNetworkRepository,
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
            //TODO need to confirmation added smallGroupId on customData
            val smallGroupId = customData["smallGroupId"] as Int
            return fetchSmallGroupAttendanceHistoryFromNetworkRepository.fetchSmallGroupAttendanceHistoryFromNetwork(
                smallGroupId
            )
        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }

    suspend fun isFetchSmallGroupAttendanceHistoryFromNetworkAPIFailed(): Boolean {
        return fetchSmallGroupAttendanceHistoryFromNetworkRepository.isFetchSmallGroupAttendanceHistoryFromNetworkAPIStatus()?.status == DEFAULT_ERROR_CODE
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_GET_ATTENDANCE_HISTORY_FROM_NETWORK
    }

}